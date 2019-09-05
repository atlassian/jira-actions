package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.dockerinfrastructure.api.browser.DockerisedChrome
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.Jira
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.JiraCoreFormula
import com.atlassian.performance.tools.jiraactions.api.*
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.page.DashboardPage
import com.atlassian.performance.tools.jiraactions.api.page.isElementPresent
import com.atlassian.performance.tools.jiraactions.api.w3c.DisabledW3cPerformanceTimeline
import com.atlassian.performance.tools.jiraactions.lib.WebDriverDiagnostics
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.RemoteWebDriver
import java.nio.file.Paths
import java.time.Clock
import java.util.*

class JiraCoreScenarioIT {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    @After
    fun waitForCleanups() {
        Thread.sleep(10000)
    }

    /**
     * During the test, you can connect to WebDriver by the VNC viewer.
     * DockerisedChrome opens the port 5900 so executing command
     * docker ps
     * will show you which port on your host is open and mapped to 5900 on the container.
     * The default password is `secret`.
     */
    @Test
    fun shouldRunScenarioWithoutErrors() {
        val version = System.getenv("JIRA_CORE_VERSION") ?: "7.13.0"
        logger.info("Testing Jira $version")
        val metrics = mutableListOf<ActionMetric>()
        val actionMeter = ActionMeter(
            virtualUser = UUID.randomUUID(),
            output = CollectionActionMetricOutput(metrics),
            clock = Clock.systemUTC(),
            w3cPerformanceTimeline = DisabledW3cPerformanceTimeline()
        )
        val userMemory = object : UserMemory {
            override fun recall(): User = User("admin", "admin")
            override fun remember(memories: Collection<User>) = throw Exception("not implemented")
        }

        val diagnoses = Paths.get("diagnoses")
        val testOutput = JiraCoreFormula.Builder()
            .version(version)
            .diagnoses(diagnoses)
            .build()
            .provision()
            .use { jira ->
                DockerisedChrome(diagnoses.resolve("recordings"))
                    .start()
                    .use useBrowser@{ browser ->
                        val driver = browser.driver
                        return@useBrowser try {
                            test(driver, jira, actionMeter, userMemory)
                        } catch (e: Exception) {
                            WebDriverDiagnostics(driver).diagnose(e)
                            throw Exception("Testing with WebDriver failed, look for diagnoses", e)
                        }
                    }
            }

        val results = metrics.map { it.result }
        assertThat(results).containsOnly(ActionResult.OK)
        val viewIssueMetrics = metrics.filter { VIEW_ISSUE.label == it.label }
        assertThat(viewIssueMetrics).allMatch { m -> m.observation != null }
        assertThat(testOutput.firstBackupPresent).isFalse()
        assertThat(testOutput.secondBackupPresent).isFalse()
    }

    private fun test(
        driver: RemoteWebDriver,
        jira: Jira,
        actionMeter: ActionMeter,
        userMemory: UserMemory
    ): TestOutput {
        val scenario = JiraCoreScenario()
        val webJira = WebJira(
            driver,
            jira.getUri(),
            userMemory.recall()!!.password
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
        createCustomDashboard(driver)
        logInAction.run()
        setupAction.run()
        actions.forEach { action ->
            action.run()
        }
        goToServices(driver, jira)
        addBackupService(driver)
        webJira.configureBackupPolicy().delete()
        goToServices(driver, jira)
        return TestOutput(
            firstBackupPresent = driver.isElementPresent(By.id("del_10001")),
            secondBackupPresent = driver.isElementPresent(By.id("del_10200"))
        )
    }

    private fun createCustomDashboard(
        driver: WebDriver
    ) {
        DashboardPage(driver).dismissAllPopups()
        val toolsCog = driver
            .findElements(By.cssSelector("#dashboard-tools-dropdown.icon-tools"))
            .singleOrNull()
            ?: driver.findElement(By.cssSelector("[aria-controls=tools-dropdown-items]"))
        toolsCog.click()
        driver.findElement(By.id("create_dashboard")).click()
        driver.findElement(By.cssSelector("[name=portalPageName]")).sendKeys("custom dashboard ${UUID.randomUUID()}")
        val addDashboardButton = driver
            .findElements(By.id("edit-entity-submit"))
            .singleOrNull()
            ?: driver.findElement(By.id("add-dashboard-submit"))
        addDashboardButton.click()
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
        driver.findElementById("serviceName").sendKeys("another backup")
        driver.findElementById("serviceClass").sendKeys("com.atlassian.jira.service.services.export.ExportService")
        driver.findElementById("addservice_submit").click()
        driver.findElementById("update_submit").click()
    }
}

private class TestOutput(
    val firstBackupPresent: Boolean,
    val secondBackupPresent: Boolean
)
