package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.presenceOfElementLocated
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.DetailView
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.IssueNavResultsView
import com.atlassian.performance.tools.jiraactions.page.issuenav.UnknownView
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.time.Duration

open class IssueNavigatorPage(
    private val driver: WebDriver,
    val jql: String
) {
    private val emptyResults = presenceOfElementLocated(By.className("no-results-hint"))

    @Deprecated("Waiting for results depend on the results view", ReplaceWith("waitForResults"))
    fun waitForIssueNavigator(): IssueNavigatorPage = waitForResults(UnknownView(driver))

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


    @Deprecated("Issue keys depend on the results view", ReplaceWith("IssueNavResultsView.listIssueKeys().toSet()"))
    open fun getIssueKeys(): Set<String> = UnknownView(driver).listIssueKeys().toSet()

    fun issueView(): IssuePage {
        return IssuePage(driver)
    }

    fun selectedIssueId(): Long {
        return IssuePage(driver).getIssueId()
    }

    protected fun getDriver(): WebDriver {
        return this.driver
    }

    @Deprecated("Total results depend on the results view", ReplaceWith("IssueNavResultsView.countResults()"))
    fun getTotalResults(): Int = UnknownView(driver).countResults() ?: 0
}
