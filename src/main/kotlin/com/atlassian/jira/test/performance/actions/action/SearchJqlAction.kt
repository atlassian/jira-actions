package com.atlassian.jira.test.performance.actions.action

import com.atlassian.jira.test.performance.actions.SEARCH_WITH_JQL
import com.atlassian.jira.test.performance.actions.SearchJqlObservation
import com.atlassian.jira.test.performance.actions.WebJira
import com.atlassian.jira.test.performance.actions.action.Action
import com.atlassian.jira.test.performance.actions.measure.ActionMeter
import com.atlassian.jira.test.performance.actions.memories.IssueKeyMemory
import com.atlassian.jira.test.performance.actions.memories.JqlMemory
import com.atlassian.jira.test.performance.actions.page.IssueNavigatorPage
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