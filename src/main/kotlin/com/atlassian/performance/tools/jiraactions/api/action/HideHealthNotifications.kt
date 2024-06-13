package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.HIDE_HEALTH_NOTIFICATIONS
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter

class HideHealthNotifications(
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {
    override fun run() {
        meter.measure(HIDE_HEALTH_NOTIFICATIONS) {
            val systemAdministrationPage = jira.administrate().system()
            jira.accessAdmin().runWithAccess {
                systemAdministrationPage
                    .troubleshootingAndSupportTools()
                    .instanceHealth()
                    .notifications()
                    .dontShowAny()
            }
        }
    }
}
