package com.atlassian.performance.tools.jiraactions.action

import com.atlassian.performance.tools.jiraactions.SET_UP
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter

class SetUpAction(
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {
    override fun run() {
        meter.measure(SET_UP) {
            jira.configureRichTextEditor().disable()
        }
    }
}