package com.atlassian.jira.test.performance.actions.action

import com.atlassian.jira.test.performance.actions.SET_UP
import com.atlassian.jira.test.performance.actions.WebJira
import com.atlassian.jira.test.performance.actions.action.Action
import com.atlassian.jira.test.performance.actions.measure.ActionMeter

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