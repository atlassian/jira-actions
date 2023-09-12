package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.VIEW_ISSUE
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.json.Json

class ViewIssueAction private constructor(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val issueKeyMemory: IssueKeyMemory?,
    private val issueMemory: IssueMemory?,
    private val jqlMemory: JqlMemory?,
    private val commentMemory: CommentMemory?
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    @Deprecated("Use ViewIssueAction.Builder instead.")
    constructor(
        jira: WebJira,
        meter: ActionMeter,
        issueKeyMemory: IssueKeyMemory?,
        issueMemory: IssueMemory?,
        jqlMemory: JqlMemory?
    ) : this(
        jira = jira,
        meter = meter,
        issueKeyMemory = issueKeyMemory,
        issueMemory = issueMemory,
        jqlMemory = jqlMemory,
        commentMemory = null
    )

    override fun run() {
        val issueKey = issueKeyMemory?.recall()
        if (issueKey == null) {
            logger.debug("Skipping View Issue action. I have no knowledge of issue keys.")
            return
        }
        val issuePage = meter.measure(
            key = VIEW_ISSUE,
            action = { jira.goToIssue(issueKey).waitForSummary() },
            observation = { _ ->
                Json.createObjectBuilder()
                    .add("issueKey", issueKey)
                    .build()
            }
        )
        val issue = Issue(
            key = issueKey,
            editable = issuePage.isEditable(),
            id = issuePage.getIssueId(),
            type = issuePage.getIssueType()
        )
        val comments = issuePage.openCommentTabPanel()
            .showAllComments()
            .getComments()

        issueMemory?.remember(setOf(issue))
        commentMemory?.remember(comments)
        jqlMemory?.observe(issuePage)
    }

    class Builder(
        private var jira: WebJira,
        private var meter: ActionMeter
    ) {
        private var issueKeyMemory: IssueKeyMemory? = null
        private var issueMemory: IssueMemory? = null
        private var jqlMemory: JqlMemory? = null
        private var commentMemory: CommentMemory? = null

        fun webJira(jira: WebJira) = apply { this.jira = jira }
        fun meter(meter: ActionMeter) = apply { this.meter = meter }
        fun issueKeyMemory(issueKeyMemory: IssueKeyMemory) = apply { this.issueKeyMemory = issueKeyMemory }
        fun issueMemory(issueMemory: IssueMemory) = apply { this.issueMemory = issueMemory }
        fun jqlMemory(jqlMemory: JqlMemory) = apply { this.jqlMemory = jqlMemory }
        fun commentMemory(commentMemory: CommentMemory) = apply { this.commentMemory = commentMemory }

        fun build() = ViewIssueAction(
            jira = jira,
            meter = meter,
            issueKeyMemory = issueKeyMemory,
            issueMemory = issueMemory,
            jqlMemory = jqlMemory,
            commentMemory = commentMemory
        )
    }
}
