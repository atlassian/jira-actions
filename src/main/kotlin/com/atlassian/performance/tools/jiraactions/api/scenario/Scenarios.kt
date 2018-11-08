package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.LogInAction
import com.atlassian.performance.tools.jiraactions.api.action.SetUpAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory

interface Scenario {
    /**
     * Different actions have different proportions. The goal is to make the traffic more realistic.
     */
    fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        meter: ActionMeter
    ): List<Action>

    /**
     *
     * Setup Jira before applying load.
     *
     * @param jira Helps to navigate to Jira pages. We assume a user is already logged in to Jira instance.
     * @param meter Measures setup action.
     */
    @JvmDefault
    fun getSetupAction(
        jira: WebJira,
        meter: ActionMeter
    ): Action {
        return SetUpAction(
            jira = jira,
            meter = meter
        )
    }

    /**
     *
     * Setup Jira before applying load.
     *
     * @param jira Helps to navigate to Jira pages.
     * @param meter Measures setup action.
     * @param userMemory The user will be used to log in to the Jira instance
     */
    @JvmDefault
    fun getLogInAction(
        jira: WebJira,
        meter: ActionMeter,
        userMemory: UserMemory
    ): Action {
        return LogInAction(
            jira = jira,
            meter = meter,
            userMemory = userMemory
        )
    }
}

fun <T> MutableList<T>.addMultiple(
    repeats: Int,
    element: T
) {
    repeat(repeats, { this.add(element) })
}