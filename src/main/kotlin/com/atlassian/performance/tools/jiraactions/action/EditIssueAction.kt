package com.atlassian.performance.tools.jiraactions.action

import com.atlassian.performance.tools.jiraactions.EDIT_ISSUE
import com.atlassian.performance.tools.jiraactions.EDIT_ISSUE_SUBMIT
import com.atlassian.performance.tools.jiraactions.IssueObservation
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.memories.IssueMemory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class EditIssueAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val issueMemory: IssueMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val issue = issueMemory.recall { it.editable && it.type != "Epic" }
        if (issue == null) {
            logger.info("Cannot edit any issue, because I didn't see any editable issues")
            return
        }
        meter.measure(EDIT_ISSUE) {
            val editIssueForm = jira
                .goToEditIssue(issue.id)
                .waitForEditIssueForm()
                .fillForm()
            meter.measure(
                key = EDIT_ISSUE_SUBMIT,
                action = { editIssueForm.submit() },
                observation = { IssueObservation(issue.key).serialize() }
            )
            return@measure editIssueForm
        }

    }
}