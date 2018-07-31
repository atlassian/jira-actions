package com.atlassian.performance.tools.jiraactions.action

import com.atlassian.performance.tools.jiraactions.SEARCH_WITH_JQL
import com.atlassian.performance.tools.jiraactions.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.page.IssueNavigatorPage
import javax.json.JsonObject

class SearchJqlAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val jqlMemory: JqlMemory,
    private val issueKeyMemory: IssueKeyMemory
) : Action {
    override fun run() {
        val jqlQuery = jqlMemory.recall()!!

        val issueNavigatorPage = meter.measure(
            key = SEARCH_WITH_JQL,
            action = { jira.goToIssueNavigator(jqlQuery).waitForIssueNavigator() },
            observation = this::observe
        )
        issueKeyMemory.remember(issueNavigatorPage.getIssueKeys())
    }

    private fun observe(
        page: IssueNavigatorPage
    ): JsonObject {
        val issueKeys = page.getIssueKeys()
        issueKeyMemory.remember(issueKeys)
        return SearchJqlObservation(page.jql, issueKeys.size, page.getTotalResults()).serialize()
    }
}