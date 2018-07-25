package com.atlassian.jira.test.performance.actions.action

import com.atlassian.jira.test.performance.actions.VIEW_DASHBOARD
import com.atlassian.jira.test.performance.actions.WebJira
import com.atlassian.jira.test.performance.actions.action.Action
import com.atlassian.jira.test.performance.actions.measure.ActionMeter

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