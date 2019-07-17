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
 * Provides Jira `Scenario`.
 * This class returns all available actions at current time.
 * It is highly possible and expected that over time these actions
 * will be different. Thus <strong>don't</strong> assume
 * actions provided by this class will never change.
 * @since 3.6.0
 */
class JiraAllActionScenario constructor() : Scenario {
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
        val searchAStar = SearchJqlStarAction(
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
            createIssue to 1,
            searchWithJql to 1,
            searchAStar to 1,
            viewIssue to 1,
            projectSummary to 1,
            viewDashboard to 1,
            editIssue to 1,
            addComment to 1,
            browseProjects to 1
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
     * @since 3.6.0
     */
    class Builder {
        private var issueKeyMemory: IssueKeyMemory? = null

        /**
         * @since 3.6.0
         */
        fun issueKeyMemory(issueKeyMemory: IssueKeyMemory) = apply { this.issueKeyMemory = issueKeyMemory }

        /**
         * @since 3.6.0
         */
        fun build(): Scenario = JiraAllActionScenario(
            issueKeyMemory = issueKeyMemory
        )
    }
}
