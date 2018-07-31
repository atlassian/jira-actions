package com.atlassian.performance.tools.jiraactions.action

import com.atlassian.performance.tools.jiraactions.VIEW_ISSUE
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.memories.Issue
import com.atlassian.performance.tools.jiraactions.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.memories.IssueMemory
import com.atlassian.performance.tools.jiraactions.memories.JqlMemory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ViewIssueAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val issueKeyMemory: IssueKeyMemory,
    private val issueMemory: IssueMemory,
    private val jqlMemory: JqlMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val issueKey = issueKeyMemory.recall()
        if (issueKey == null) {
            logger.info("Skipping View Issue action. I have no knowledge of issue keys.")
            return
        }
        val issuePage = meter.measure(VIEW_ISSUE) {
            jira.goToIssue(issueKey).waitForSummary()
        }
        val issue = Issue(
            key = issueKey,
            editable = issuePage.isEditable(),
            id = issuePage.getIssueId(),
            type = issuePage.getIssueType()
        )
        issueMemory.remember(setOf(issue))
        jqlMemory.observe(issuePage)
    }
}