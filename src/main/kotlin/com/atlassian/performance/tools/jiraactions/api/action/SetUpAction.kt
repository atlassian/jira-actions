package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SET_UP
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter

class SetUpAction(
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {
    override fun run() {
        meter.measure(SET_UP) {
            jira.configureRichTextEditor().disable()
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
