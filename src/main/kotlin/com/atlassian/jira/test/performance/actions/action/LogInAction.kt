package com.atlassian.jira.test.performance.actions.action

import com.atlassian.jira.test.performance.actions.LOG_IN
import com.atlassian.jira.test.performance.actions.WebJira
import com.atlassian.jira.test.performance.actions.action.Action
import com.atlassian.jira.test.performance.actions.measure.ActionMeter
import com.atlassian.jira.test.performance.actions.memories.UserMemory

class LogInAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val userMemory: UserMemory
) : Action {
    override fun run() {
        val user = userMemory.recall()!!
        meter.measure(LOG_IN) {
            val dashboardPage = jira.goToLogin().logIn(user)
            dashboardPage.waitForDashboard()
        }
    }
}