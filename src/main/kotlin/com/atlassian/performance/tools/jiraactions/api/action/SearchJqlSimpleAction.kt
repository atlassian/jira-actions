package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_JQL_SIMPLE
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorView
import com.atlassian.performance.tools.jiraactions.jql.BuiltInJQL
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Predicate
import javax.json.JsonObject

class SearchJqlSimpleAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val jqlMemory: JqlMemory,
    private val issueKeyMemory: IssueKeyMemory,
    private val view: IssueNavigatorView = IssueNavigatorView.DETAIL_VIEW
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val jqlQueries = jqlMemory.recallByTag(Predicate { it != BuiltInJQL.GENERIC_WIDE.name && it != BuiltInJQL.REPORTERS.name })
        if (jqlQueries == null) {
            logger.debug("Skipping ${SEARCH_JQL_SIMPLE.label} action. I have no knowledge of simple JQL queries.")
            return
        }

        val issueNavigatorPage = meter.measure(
            key = SEARCH_JQL_SIMPLE,
            action = { jira.goToIssueNavigator(jqlQueries).switchTo(view).waitForIssueNavigator() },
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
