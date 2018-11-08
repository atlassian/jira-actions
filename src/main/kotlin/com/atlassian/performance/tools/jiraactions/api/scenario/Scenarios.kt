package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter

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