package com.atlassian.performance.tools.jiraactions.scenario

import com.atlassian.performance.tools.jiraactions.SeededRandom
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.action.Action
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter

interface Scenario {
    /**
     * Different actions have different proportions. The goal is to make the traffic more realistic.
     */
    fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        meter: ActionMeter
    ): List<Action>
}

fun <T> MutableList<T>.addMultiple(
    repeats: Int,
    element: T
) {
    repeat(repeats, { this.add(element) })
}