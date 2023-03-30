package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_JQL_SIMPLE
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory.Companion.simple

@Deprecated(
    "Use SearchIssues.Builder",
    ReplaceWith(
        "SearchIssues.Builder(jira, meter, SeededRandom()).actionType(SEARCH_JQL_SIMPLE).jqlMemory(jqlMemory.simple()).issueKeyMemory(issueKeyMemory).build()",
        "com.atlassian.performance.tools.jiraactions.api.SEARCH_JQL_SIMPLE",
        "com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory.Companion.simple",
        "com.atlassian.performance.tools.jiraactions.api.SeededRandom"
    )
)
class SearchJqlSimpleAction(
    jira: WebJira,
    meter: ActionMeter,
    jqlMemory: JqlMemory,
    issueKeyMemory: IssueKeyMemory
) : Action {

    private val action = SearchIssues.Builder(jira, meter, SeededRandom())
        .actionType(SEARCH_JQL_SIMPLE)
        .jqlMemory(jqlMemory.simple())
        .issueKeyMemory(issueKeyMemory)
        .build()

    override fun run() = action.run()
}
