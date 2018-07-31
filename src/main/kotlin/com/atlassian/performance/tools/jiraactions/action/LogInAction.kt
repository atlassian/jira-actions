package com.atlassian.performance.tools.jiraactions.action

import com.atlassian.performance.tools.jiraactions.LOG_IN
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.memories.UserMemory

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