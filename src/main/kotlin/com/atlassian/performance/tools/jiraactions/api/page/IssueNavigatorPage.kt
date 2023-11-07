package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.presenceOfElementLocated
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.IssueNavResultsView
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.time.Duration

open class IssueNavigatorPage(
    private val driver: WebDriver,
    val jql: String
) {
    private val emptyResults = presenceOfElementLocated(By.className("no-results-hint"))

    fun waitForResults(results: IssueNavResultsView): IssueNavigatorPage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            Duration.ofSeconds(10),
            or(
                results.detectResults(),
                emptyResults,
                jiraErrors.anyCommonErrorNative()
            )
        )
        return this
    }

    fun issueView(): IssuePage {
        return IssuePage(driver)
    }

    fun selectedIssueId(): Long {
        return IssuePage(driver).getIssueId()
    }

    protected fun getDriver(): WebDriver {
        return this.driver
    }

}
