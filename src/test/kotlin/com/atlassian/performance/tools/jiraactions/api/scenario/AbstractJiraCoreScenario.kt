package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.dockerinfrastructure.api.jira.Jira
import com.atlassian.performance.tools.jiraactions.api.*
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.measure.DrillDownHook
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.page.isElementPresent
import com.atlassian.performance.tools.jiraactions.api.page.tolerateDirtyFormsOnCurrentPage
import com.atlassian.performance.tools.jiraactions.api.w3c.JavascriptW3cPerformanceTimeline
import com.atlassian.performance.tools.jiraactions.api.webdriver.sendKeysAndValidate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.OutputType
import org.openqa.selenium.remote.RemoteWebDriver
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

abstract class AbstractJiraCoreScenario {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    protected val jiraVersion = System.getenv("JIRA_SOFTWARE_VERSION") ?: "8.0.0"

    fun shouldRunScenarioWithoutErrors(jira: Jira, driver: RemoteWebDriver, rng: SeededRandom) {
        logger.info("Testing Jira $jiraVersion")
        val scenario = JiraCoreScenario()
        val metrics = mutableListOf<ActionMetric>()
        val actionMeter = ActionMeter.Builder(
            output = CollectionActionMetricOutput(metrics)
        ).appendPostMetricHook(
            DrillDownHook(
                JavascriptW3cPerformanceTimeline.Builder(driver as JavascriptExecutor)
                    .recordAll()
                    .build()
            )
        ).build()

        val user = User("admin", "admin")
        val userMemory = object : UserMemory {
            override fun recall(): User {
                return user
            }

            override fun remember(memories: Collection<User>) {
                throw Exception("not implemented")
            }
        }

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
            rng,
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

        val firstBackupElement = driver.isElementPresent(By.id("del_10001"))
        val secondBackupElement = driver.isElementPresent(By.id("del_10200"))

        assertThat(metrics).`as`("all results are OK").allMatch { it.result == ActionResult.OK }
        assertDrilldown(metrics)

        val viewIssueMetrics = metrics.filter { VIEW_ISSUE.label == it.label }
        assertThat(viewIssueMetrics)
            .`as`("view issue metrics")
            .isNotEmpty()
            .allMatch { m -> m.observation != null }
        assertThat(firstBackupElement).isFalse()
        assertThat(secondBackupElement).isFalse()
    }

    private fun assertDrilldown(metrics: List<ActionMetric>) {
        val navigationsPerMetric = metrics.map { it.drilldown?.navigations ?: emptyList() }
        assertThat(navigationsPerMetric).`as`("all results contain timings in drilldown").hasSize(metrics.size)
        val timeOrigin = metrics[0].drilldown?.timeOrigin
        assertThat(timeOrigin).isNotNull()
        val dateTimeOrigin = ZonedDateTime.ofInstant(timeOrigin, ZoneId.of("UTC"))
        assertThat(dateTimeOrigin.year).isBetween(2024, 2060)
    }

    private fun goToServices(driver: RemoteWebDriver, jira: Jira) {
        driver
            .navigate()
            .to(
                jira.getUri()
                    .resolve("secure/admin/ViewServices!default.jspa")
                    .toURL()
            )

        if (driver.isElementPresent(By.id("login-form-authenticatePassword"))) {
            driver.findElementById("login-form-authenticatePassword").sendKeys("admin")
            driver.findElement(By.id("login-form-submit")).click()
        }
        driver.tolerateDirtyFormsOnCurrentPage()
    }

    private fun addBackupService(driver: RemoteWebDriver) {
        driver.findElement(By.id("serviceName")).sendKeysAndValidate(driver, "another backup")
        driver.findElement(By.id("serviceClass"))
            .sendKeysAndValidate(driver, "com.atlassian.jira.service.services.export.ExportService")

        driver.findElementById("addservice_submit").click()

        try {
            driver.findElementById("update_submit").click()
        } catch (e: Exception) {
            // this action was still flaky in CI before the introduction of sendKeys,
            // we can remove this try/catch once we are sure it's stable
            println(driver.getScreenshotAs(OutputType.BASE64))
            throw e
        }
    }
}
