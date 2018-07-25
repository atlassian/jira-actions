package com.atlassian.jira.test.performance.actions.action

import com.atlassian.jira.test.performance.actions.EDIT_ISSUE
import com.atlassian.jira.test.performance.actions.EDIT_ISSUE_SUBMIT
import com.atlassian.jira.test.performance.actions.IssueObservation
import com.atlassian.jira.test.performance.actions.WebJira
import com.atlassian.jira.test.performance.actions.measure.ActionMeter
import com.atlassian.jira.test.performance.actions.memories.IssueMemory
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