package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.*
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.*
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.DetailView
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.ListView

/**
 * Provides Jira Core specific `Scenario`.
 * @since 3.3.0
 */
class JiraCoreScenario constructor() : Scenario {
    private lateinit var issueKeyMemory: IssueKeyMemory

    private constructor(issueKeyMemory: IssueKeyMemory?) : this() {
        issueKeyMemory?.let { this.issueKeyMemory = it }
    }

    override fun getActions(jira: WebJira, seededRandom: SeededRandom, meter: ActionMeter): List<Action> {
        initializeIssueKeyMemory(seededRandom)
        val jqlMemory = AdaptiveJqlMemory(seededRandom)
        val projectMemory = JqlRememberingProjectMemory
            .Builder(
                delegate = AdaptiveProjectMemory(random = seededRandom),
                jqlMemory = LimitedJqlMemory(
                    delegate = jqlMemory,
                    limit = 3
                )
            )
            .build()
        val issueMemory = AdaptiveIssueMemory(issueKeyMemory, seededRandom)
        val commentMemory = AdaptiveCommentMemory(seededRandom)

        val scenario: MutableList<Action> = mutableListOf()

        val createIssue = CreateIssueAction(
            jira = jira,
            meter = meter,
            seededRandom = seededRandom,
            projectMemory = projectMemory
        )
        val searchWithJql = SearchJqlAction(
            jira = jira,
            meter = meter,
            jqlMemory = jqlMemory,
            issueKeyMemory = issueKeyMemory,
            view = seededRandom.pick(listOf(DetailView(jira.driver), ListView(jira.driver)))!!
        )
        val viewIssue = ViewIssueAction.Builder(jira, meter)
            .issueKeyMemory(issueKeyMemory)
            .issueMemory(issueMemory)
            .jqlMemory(jqlMemory)
            .commentMemory(commentMemory)
            .build()

        val projectSummary = ProjectSummaryAction(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        )
        val viewDashboard = ViewDashboardAction(
            jira = jira,
            meter = meter
        )
        val editIssue = EditIssueAction(
            jira = jira,
            meter = meter,
            issueMemory = issueMemory
        )
        val addComment = AddCommentAction(
            jira = jira,
            meter = meter,
            issueMemory = issueMemory
        )
        val browseProjects = BrowseProjectsAction(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        )

        val viewComment = ViewCommentAction(
            jira = jira,
            meter = meter,
            commentMemory = commentMemory
        )

        val viewHistoryTabAction = ViewHistoryTabAction(
            jira = jira,
            meter = meter,
            issueKeyMemory = issueKeyMemory
        )

        val actionProportions = mapOf(
            createIssue to 5,
            searchWithJql to 20,
            viewIssue to 55,
            projectSummary to 5,
            viewDashboard to 15, // note that we may end up generating more views if we need to display top nav
            editIssue to 5,
            addComment to 2,
            browseProjects to 5,
            viewComment to 5,
            viewHistoryTabAction to 5
        )

        actionProportions.entries.forEach { scenario.addMultiple(element = it.key, repeats = it.value) }
        scenario.shuffle(seededRandom.random)
        return scenario
    }

    private fun initializeIssueKeyMemory(seededRandom: SeededRandom) {
        synchronized(this) {
            if (this::issueKeyMemory.isInitialized.not()) {
                issueKeyMemory = AdaptiveIssueKeyMemory(seededRandom)
            }
        }
    }

    /**
     * You can use `Builder` to share memories between Scenarios.
     * @since 3.3.0
     */
    class Builder {
        private var issueKeyMemory: IssueKeyMemory? = null

        /**
         * @since 3.3.0
         */
        fun issueKeyMemory(issueKeyMemory: IssueKeyMemory) = apply { this.issueKeyMemory = issueKeyMemory }

        /**
         * @since 3.3.0
         */
        fun build(): Scenario = JiraCoreScenario(
            issueKeyMemory = issueKeyMemory
        )
    }
}
