package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.LOG_IN
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.page.NotificationPopUps

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
            NotificationPopUps(jira.driver)
                .dismissHealthCheckNotifications()
                .dismissJiraHelpTips()
                .dismissPostSetup()
                .disableNpsFeedback()
                .dismissAuiFlags()
                .dismissFindYourWorkFaster()
                .waitUntilAuiFlagsAreGone()
        }
    }
}
