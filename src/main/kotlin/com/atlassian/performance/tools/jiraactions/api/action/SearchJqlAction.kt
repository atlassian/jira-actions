package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_JQL_CHANGELOG
import com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.json.JsonObject

class SearchJqlAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val jqlMemory: JqlMemory,
    private val issueKeyMemory: IssueKeyMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val jqlQuery = jqlMemory.recall()
        if (jqlQuery == null) {
            logger.debug("Skipping ${SEARCH_WITH_JQL.label} action. I have no knowledge of JQL queries.")
            return
        }

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
