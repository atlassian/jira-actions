package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.*
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory

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
        val projectMemory = AdaptiveProjectMemory(random = seededRandom)
        val jqlMemory = AdaptiveJqlMemory(seededRandom)
        val issueMemory = AdaptiveIssueMemory(issueKeyMemory, seededRandom)

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
            issueKeyMemory = issueKeyMemory
        )
        val viewIssue = ViewIssueAction(
            jira = jira,
            meter = meter,
            issueKeyMemory = issueKeyMemory,
            issueMemory = issueMemory,
            jqlMemory = jqlMemory
        )
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

        val actionProportions = mapOf(
            createIssue to 5,
            searchWithJql to 20,
            viewIssue to 55,
            projectSummary to 5,
            viewDashboard to 10,
            editIssue to 5,
            addComment to 2,
            browseProjects to 5
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