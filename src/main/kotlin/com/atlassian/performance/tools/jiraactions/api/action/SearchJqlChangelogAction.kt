package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_JQL_CHANGELOG
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.DetailView
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.IssueNavResultsView
import com.atlassian.performance.tools.jiraactions.jql.BuiltInJQL
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Predicate
import javax.json.JsonObject

class SearchJqlChangelogAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val jqlMemory: JqlMemory,
    private val issueKeyMemory: IssueKeyMemory,
    private val view: IssueNavResultsView = DetailView(jira.driver)
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val jqlQuery = jqlMemory.recallByTag(Predicate { it == BuiltInJQL.REPORTERS.name })
        if (jqlQuery == null) {
            logger.debug("Skipping ${SEARCH_JQL_CHANGELOG.label} action. I have no knowledge of changelog-specific JQL queries.")
            return
        }

        val issueNavigatorPage = meter.measure(
            key = SEARCH_JQL_CHANGELOG,
            action = { jira.goToIssueNavigator(jqlQuery).also { view.switchToView() }.waitForResults(view) },
            observation = this::observe
        )
        issueKeyMemory.remember(view.listIssueKeys())
    }

    private fun observe(
        page: IssueNavigatorPage
    ): JsonObject {
        val issueKeys = view.listIssueKeys()
        issueKeyMemory.remember(issueKeys)
        return SearchJqlObservation(page.jql, issueKeys.size, view.countResults() ?: 0).serialize()
    }
}
