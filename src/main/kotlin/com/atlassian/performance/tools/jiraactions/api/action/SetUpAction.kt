package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SET_UP
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter

@Deprecated("Use [HideHealthNotifications] and/or [DisableRichTextEditor] for fine-grained control and measurements")
class SetUpAction(
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {

    override fun run() {
        meter.measure(SET_UP) {
            DisableRichTextEditor(jira, meter).run()
            HideHealthNotifications(jira, meter).run()
        }
    }
}
