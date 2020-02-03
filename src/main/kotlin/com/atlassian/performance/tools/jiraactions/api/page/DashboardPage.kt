package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.page.IssueCreateDialog
import com.atlassian.performance.tools.jiraactions.page.NotificationPopUps
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

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
            Duration.ofSeconds(60),
            or(
                and(
                    presenceOfElementLocated(By.className("page-type-dashboard")),
                    CheckIFrame()
                ),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        return this
    }

    internal fun openIssueCreateDialog(): IssueCreateDialog {
        driver.findElement(By.id("create_link")).click()
        return IssueCreateDialog(driver)
    }

    internal fun getPopUps(): NotificationPopUps {
        return popUps
    }

    private class CheckIFrame : ExpectedCondition<Boolean> {
        override fun apply(input: WebDriver?): Boolean? {
            input as JavascriptExecutor
            //we currently support only single iframe on dashboard in this check
            return input.executeScript(
                """
                if (typeof $ === 'undefined') { //wait for jquery
                    return false;
                }
                iframes = $('#dashboard').find('iframe');
                return iframes.length === 1 && iframes.contents().find('body').children().length > 0
                """
            ) as Boolean
        }
    }
}
