package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.VIEW_COMMENT
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.CommentMemory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.json.Json

class ViewCommentAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val commentMemory: CommentMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val comment = commentMemory.recall()
        if (comment == null) {
            logger.debug("Skipping View Comment action. I have no knowledge of comments.")
            return
        }

        meter.measure(
            key = VIEW_COMMENT,
            action = { jira.goToComment(comment.url).validateCommentIsFocused(comment.id) },
            observation = { page ->
                Json.createObjectBuilder()
                    .add("issueKey", page.getIssueKey())
                    .add("commentId", comment.id)
                    .build()
            }
        )
    }
}
