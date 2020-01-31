package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.dockerinfrastructure.api.browser.DockerisedChrome
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.Jira
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.JiraCoreFormula
import com.atlassian.performance.tools.jiraactions.api.*
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.page.isElementPresent
import com.atlassian.performance.tools.jiraactions.api.page.wait
import com.atlassian.performance.tools.jiraactions.api.w3c.DisabledW3cPerformanceTimeline
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.nio.file.Paths
import java.time.Clock
import java.util.*

class JiraCoreScenarioIT {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    /**
     * During the test, you can connect to WebDriver by the VNC viewer.
     * DockerisedChrome opens the port 5900 so executing command
     * docker ps
     * will show you which port on your host is open and mapped to 5900 on the container.
     * The default password is `secret`.
     */
    @Test
    fun shouldRunScenarioWithoutErrors() {
        val version = System.getenv("JIRA_SOFTWARE_VERSION") ?: "8.0.0"
        logger.info("Testing Jira $version")
        val scenario = JiraCoreScenario()
        val metrics = mutableListOf<ActionMetric>()
        val actionMeter = ActionMeter(
            virtualUser = UUID.randomUUID(),
            output = CollectionActionMetricOutput(metrics),
            clock = Clock.systemUTC(),
            w3cPerformanceTimeline = DisabledW3cPerformanceTimeline()
        )
        val user = User("admin", "admin")
        val userMemory = object : UserMemory {
            override fun recall(): User {
                return user
            }

            override fun remember(memories: Collection<User>) {
                throw Exception("not implemented")
            }
        }
        var firstBackupElement = true
        var secondBackupElement = true

        JiraCoreFormula.Builder()
            .version(version)
            .build()
            .provision()
            .use { jira ->
                val recordings = Paths.get("build/diagnoses/recordings/" + this::class.java.simpleName)
                DockerisedChrome(recordings).start().use { browser ->
                    val driver = browser.driver
                    val webJira = WebJira(
                        driver,
                        jira.getUri(),
                        user.password
                    )
                    val logInAction = scenario.getLogInAction(
                        webJira,
                        actionMeter,
                        userMemory
                    )
                    val setupAction = scenario.getSetupAction(
                        webJira,
                        actionMeter
                    )
                    val actions = scenario.getActions(
                        webJira,
                        SeededRandom(123),
                        actionMeter
                    )

                    logInAction.run()
                    setupAction.run()
                    actions.forEach { action ->
                        action.run()
                    }

                    goToServices(driver, jira)
                    addBackupService(driver)
                    webJira.configureBackupPolicy().delete()
                    goToServices(driver, jira)

                    val firstBackupId = "del_10001"
                    val secondBackupId = "del_10200"
                    firstBackupElement = driver.isElementPresent(By.id(firstBackupId))
                    secondBackupElement = driver.isElementPresent(By.id(secondBackupId))
                }
            }

        val results = metrics.map { metric ->
            metric.result
        }
        Assertions.assertThat(results).containsOnly(ActionResult.OK)
        val viewIssueMetrics = metrics.filter {
            VIEW_ISSUE.label.equals(it.label)
        }
        Assertions.assertThat(viewIssueMetrics).allMatch { m -> m.observation != null }
        Assertions.assertThat(firstBackupElement).isFalse()
        Assertions.assertThat(secondBackupElement).isFalse()
    }

    private fun goToServices(driver: RemoteWebDriver, jira: Jira) {
        driver
            .navigate()
            .to(jira.getUri()
                .resolve("secure/admin/ViewServices!default.jspa")
                .toURL())

        if (driver.isElementPresent(By.id("login-form-authenticatePassword"))) {
            driver.findElementById("login-form-authenticatePassword").sendKeys("admin")
            driver.findElement(By.id("login-form-submit")).click()
        }
    }

    private fun addBackupService(driver: RemoteWebDriver) {
        val serviceNameInput = 
            driver.wait(ExpectedConditions.elementToBeClickable(driver.findElementById("serviceName")))
        serviceNameInput.click()
        val serviceName = "another backup"
        serviceNameInput.sendKeys(serviceName)
        
        val serviceClass = driver.findElementById("serviceClass")
        serviceClass.click()
        serviceClass.sendKeys("com.atlassian.jira.service.services.export.ExportService")
        
        driver.findElementById("addservice_submit").click()
        try {
            driver.findElementById("update_submit").click()
        } catch (e: Exception) {
            println((driver as RemoteWebDriver).getScreenshotAs(OutputType.BASE64))
            throw e
        }
    }
}
