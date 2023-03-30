package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory

@Deprecated(
    "Use SearchIssues.Builder",
    ReplaceWith(
        "SearchIssues.Builder(jira, meter, SeededRandom()).actionType(SEARCH_WITH_JQL).jqlMemory(jqlMemory).issueKeyMemory(issueKeyMemory).build()",
        "com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL",
        "com.atlassian.performance.tools.jiraactions.api.SeededRandom"
    )
)
class SearchJqlAction(
    jira: WebJira,
    meter: ActionMeter,
    jqlMemory: JqlMemory,
    issueKeyMemory: IssueKeyMemory
) : Action {

    private val action = SearchIssues.Builder(jira, meter, SeededRandom())
        .actionType(SEARCH_WITH_JQL)
        .jqlMemory(jqlMemory)
        .issueKeyMemory(issueKeyMemory)
        .build()

    override fun run() = action.run()
}
