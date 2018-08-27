package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.ADD_COMMENT
import com.atlassian.performance.tools.jiraactions.api.ADD_COMMENT_SUBMIT
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueMemory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class AddCommentAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val issueMemory: IssueMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val issue = issueMemory.recall()
        if (issue == null) {
            logger.info("Cannot add a comment, because I didn't see any issues yet")
            return
        }
        meter.measure(ADD_COMMENT) {
            val commentForm = jira.goToCommentForm(issue.id).waitForButton().enterCommentText("SNARKY REMARK")
            meter.measure(ADD_COMMENT_SUBMIT) {
                commentForm.submit().waitForSummary()
            }
        }
    }
}