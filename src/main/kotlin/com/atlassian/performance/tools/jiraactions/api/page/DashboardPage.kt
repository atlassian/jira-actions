package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated

class DashboardPage(
    private val driver: WebDriver
) {
    private val jiraErrors = JiraErrors(driver)
    private val popUps = NotificationPopUps(driver)

    fun dismissAllPopups() {
        popUps
            .disableNpsFeedback()
            .dismissJiraHelpTips()
            .dismissPostSetup()
            .waitUntilAuiFlagsAreGone()
    }

    fun waitForDashboard(): DashboardPage {
        driver.wait(
            or(
                presenceOfElementLocated(By.className("page-type-dashboard")),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        return this
    }

    internal fun getPopUps(): NotificationPopUps {
        return popUps
    }
}
