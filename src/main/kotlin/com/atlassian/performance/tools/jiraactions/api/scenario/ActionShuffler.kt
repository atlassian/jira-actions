package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.BrowseProjectsAction
import com.atlassian.performance.tools.jiraactions.api.action.CreateIssueAction
import com.atlassian.performance.tools.jiraactions.api.action.SearchJqlAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage
import kotlin.reflect.KClass

internal class ActionShuffler {
    private class FixedJqlMemory(val jql: String) : JqlMemory {
        override fun observe(issuePage: IssuePage) {
            throw UnsupportedOperationException()
        }

        override fun recall(): String? {
            return jql
        }

        override fun remember(memories: Collection<String>) {
            throw UnsupportedOperationException()
        }
    }

    companion object {
        fun createRandomisedScenario(seededRandom: SeededRandom, actionProportions: Map<Action, Int>,
                                     issueKeyMemoriser: Action): List<Action> {
            //createIssue needs a project - browserProject goes first
            //viewIssue needs to have issues - createIssues goes second
            return createRandomisedScenario(seededRandom, actionProportions, issueKeyMemoriser, BrowseProjectsAction::class, CreateIssueAction::class)
        }

        fun findIssueKeysWithJql(jira: WebJira, meter: ActionMeter, issueKeyMemory: IssueKeyMemory): SearchJqlAction {
            return SearchJqlAction(
                jira = jira,
                meter = meter,
                jqlMemory = FixedJqlMemory("project is not EMPTY"),
                issueKeyMemory = issueKeyMemory
            )
        }

        private fun createRandomisedScenario(seededRandom: SeededRandom, actionProportions: Map<Action, Int>,
                                     issueKeyDiscoverer: Action, vararg actions: KClass<out Action>): List<Action> {
            val initialActions = findActions(actionProportions, *actions)

            val scenario: MutableList<Action> = mutableListOf()

            val actionProportionsToRandomise = deductActionCount(actionProportions, initialActions)
            actionProportionsToRandomise.entries.forEach { scenario.addMultiple(element = it.key, repeats = it.value) }
            scenario.shuffle(seededRandom.random)

            //viewIssue needs to remember isssues - issueKeyDiscoverer goes after all actions
            scenario.addAll(0, initialActions.plus(issueKeyDiscoverer))
            return scenario
        }

        private fun findActions(actionProportions: Map<Action, Int>, vararg actions: KClass<out Action>): List<Action> {
            return actions
                .mapNotNull { findAction(actionProportions, it) }
        }

        private fun deductActionCount(actionProportions: Map<Action, Int>, actions: List<Action>): MutableMap<Action, Int> {
            val modifiedActionProportions = actionProportions.toMutableMap()

            actions.forEach {
                val originalCount = actionProportions.getValue(it)
                modifiedActionProportions[it] = originalCount-1
            }
            return modifiedActionProportions
        }

        private fun <T : Action> findAction(actionProportions: Map<Action, Int>, kClass: KClass<T>): T? {
            @Suppress("UNCHECKED_CAST")
            return actionProportions.keys.find { kClass.java.isInstance(it) } as T?
        }
    }
}
