package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.VIEW_DASHBOARD
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter

class ViewDashboardAction(
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {
    override fun run() {
        meter.measure(VIEW_DASHBOARD) {
            jira.goToDashboard().waitForDashboard()
        }
    }
}