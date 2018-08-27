package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.EDIT_ISSUE
import com.atlassian.performance.tools.jiraactions.api.EDIT_ISSUE_SUBMIT
import com.atlassian.performance.tools.jiraactions.api.observation.IssueObservation
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueMemory
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