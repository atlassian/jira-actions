package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.LOG_IN
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory

class LogInAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val userMemory: UserMemory
) : Action {
    override fun run() {
        val user = userMemory.recall()!!
        meter.measure(LOG_IN) {
            val dashboardPage = jira.goToLogin().logIn(user)
            val dashboard = dashboardPage.waitForDashboard()
            dashboard.getPopUps()
                .dismissHealthCheckNotifications()
                .dismissAuiFlags()
                .disableNpsFeedback()
                .dismissFindYourWorkFaster()
            dashboard
        }
    }
}
