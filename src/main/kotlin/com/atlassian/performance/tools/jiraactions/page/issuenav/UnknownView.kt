package com.atlassian.performance.tools.jiraactions.page.issuenav

import com.atlassian.performance.seleniumjs.NativeExpectedCondition
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.DetailView
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.IssueNavResultsView
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.ListView
import org.openqa.selenium.WebDriver

class UnknownView(
    driver: WebDriver
) : IssueNavResultsView {

    private val detail = DetailView(driver)
    private val list = ListView(driver)

    override fun switchToView() {
        // unknown
    }

    override fun detectResults(): NativeExpectedCondition = or(
        detail.detectResults(),
        list.detectResults()
    )

    override fun countResults(): Int? = detail.countResults() ?: list.countResults()

    override fun listIssueKeys(): List<String> = detail.listIssueKeys() + list.listIssueKeys()
}
