package com.atlassian.jira.test.performance.actions.scenario

import com.atlassian.jira.test.performance.actions.SeededRandom
import com.atlassian.jira.test.performance.actions.WebJira
import com.atlassian.jira.test.performance.actions.action.Action
import com.atlassian.jira.test.performance.actions.measure.ActionMeter

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