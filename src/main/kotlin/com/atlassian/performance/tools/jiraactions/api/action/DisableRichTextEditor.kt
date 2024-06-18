package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.DISABLE_RICH_TEXT_EDITOR
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter

/**
 * @since 3.28.0
 */
class DisableRichTextEditor(
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {
    override fun run() {
        meter.measure(DISABLE_RICH_TEXT_EDITOR) {
            jira.configureRichTextEditor().disable()
        }
    }
}
