package com.atlassian.performance.tools.jiraactions.action

import com.atlassian.performance.tools.jiraactions.VIEW_DASHBOARD
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter

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