package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.DetailView
import com.atlassian.performance.tools.jiraactions.api.page.issuenav.IssueNavResultsView
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.json.Json
import javax.json.JsonObject

class SearchIssues private constructor(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val actionType: ActionType<SearchJqlObservation>,
    private val jqlMemory: JqlMemory,
    private val issueKeyMemory: IssueKeyMemory,
    private val desiredView: IssueNavResultsView
) : Action {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val jqlQuery = jqlMemory.recall()
        if (jqlQuery == null) {
            logger.debug("Skipping ${actionType.label} action, because I cannot recall any JQL queries.")
            return
        }
        meter.measure(
            key = actionType,
            action = { search(jqlQuery) },
            observation = this::observe
        )
    }

    private fun search(jqlQuery: String) = jira
        .goToIssueNavigator(jqlQuery)
        .also { switchResultsView() }
        .waitForResults(desiredView)

    private fun switchResultsView() {
        meter.measure(
            switchActionType,
            {
                val observation = SwitchObservation.Builder(desiredView.javaClass.simpleName)
                val selectedBefore = desiredView.isSelected()
                observation.selectedBefore(selectedBefore)
                if (!selectedBefore) {
                    desiredView.switchToView()
                    observation.switched()
                }
                observation
            },
            { observation ->
                observation
                    .selectedAfter(desiredView.isSelected())
                    .build()
                    .serialize()
            }
        )
    }

    private fun observe(page: IssueNavigatorPage): JsonObject {
        val issueKeys = desiredView.listIssueKeys()
        issueKeyMemory.remember(issueKeys)
        return SearchJqlObservation(page.jql, issueKeys.size, desiredView.countResults() ?: -1).serialize()
    }

    class Builder(
        private var jira: WebJira,
        private var meter: ActionMeter,
        seededRandom: SeededRandom
    ) {

        private var actionType: ActionType<SearchJqlObservation> = SEARCH_WITH_JQL
        private var jqlMemory: JqlMemory = AdaptiveJqlMemory(seededRandom)
        private var issueKeyMemory: IssueKeyMemory = AdaptiveIssueKeyMemory(seededRandom)
        private var desiredView: IssueNavResultsView = DetailView(jira.driver)

        fun actionType(actionType: ActionType<SearchJqlObservation>) = apply { this.actionType = actionType }
        fun jqlMemory(jqlMemory: JqlMemory) = apply { this.jqlMemory = jqlMemory }
        fun issueKeyMemory(issueKeyMemory: IssueKeyMemory) = apply { this.issueKeyMemory = issueKeyMemory }
        fun desiredView(desiredView: IssueNavResultsView) = apply { this.desiredView = desiredView }

        fun build(): Action = SearchIssues(jira, meter, actionType, jqlMemory, issueKeyMemory, desiredView)
    }

    private val switchActionType = ActionType("Switch issue nav view") { SwitchObservation(it) }

    private class SwitchObservation(
        val desiredView: String,
        val selectedBefore: Boolean,
        val selectedAfter: Boolean,
        val switched: Boolean
    ) {
        constructor(json: JsonObject) : this(
            json.getString("desiredView"),
            json.getBoolean("selectedBefore"),
            json.getBoolean("selectedAfter"),
            json.getBoolean("switched")
        )

        fun serialize(): JsonObject = Json.createObjectBuilder()
            .add("desiredView", desiredView)
            .add("selectedBefore", selectedBefore)
            .add("selectedAfter", selectedAfter)
            .add("switched", switched)
            .build()

        class Builder(
            private var desiredView: String
        ) {
            private var selectedBefore: Boolean = false
            private var selectedAfter: Boolean = false
            private var switched: Boolean = false

            fun selectedBefore(selectedBefore: Boolean) = apply { this.selectedBefore = selectedBefore }
            fun selectedAfter(selectedAfter: Boolean) = apply { this.selectedAfter = selectedAfter }
            fun switched() = apply { this.switched = true }

            fun build() = SwitchObservation(desiredView, switched, selectedBefore, selectedAfter)
        }
    }

}
