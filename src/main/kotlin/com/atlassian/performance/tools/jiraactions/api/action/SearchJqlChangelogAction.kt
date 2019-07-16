package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import com.atlassian.performance.tools.jiraactions.jql.BuiltInJQL
import com.atlassian.performance.tools.jiraactions.api.SEARCH_JQL_CHANGELOG
import java.util.function.Predicate
import javax.json.JsonObject

class SearchJqlChangelogAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val jqlMemory: JqlMemory,
    private val issueKeyMemory: IssueKeyMemory
) : Action {

    override fun run() {
        val jqlQuery = jqlMemory.recallByTag(Predicate { it == BuiltInJQL.REPORTERS.name })!!
        val issueNavigatorPage = meter.measure(
            key = SEARCH_JQL_CHANGELOG,
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
