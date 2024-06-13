package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.DISABLE_RTE
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter

class DisableRichTextEditor(
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {
    override fun run() {
        meter.measure(DISABLE_RTE) {
            jira.configureRichTextEditor().disable()
        }
    }
}
