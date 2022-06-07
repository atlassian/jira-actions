package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.VIEW_HISTORY_TAB
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.observation.IssueObservation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ViewHistoryTabAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val issueKeyMemory: IssueKeyMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val issueKey = issueKeyMemory.recall()
        if (issueKey == null) {
            logger.debug("Skipping View History Tab action. I have no knowledge of issue keys.")
            return
        }

        val page = jira.goToIssue(issueKey).waitForSummary()
        meter.measure(
            key = VIEW_HISTORY_TAB,
            action = { page.openHistoryTabPanel().showAllHistoryEntries() },
            observation = { IssueObservation(issueKey).serialize() }
        )
    }
}
