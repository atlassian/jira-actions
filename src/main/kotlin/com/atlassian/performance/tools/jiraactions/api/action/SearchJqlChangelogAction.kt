package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_JQL_CHANGELOG
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory.Companion.changelog
import com.atlassian.performance.tools.jiraactions.jql.BuiltInJQL
import com.atlassian.performance.tools.jiraactions.memories.jql.TagSelectiveJqlMemory
import java.util.function.Predicate

@Deprecated(
    "Use SearchIssues.Builder",
    ReplaceWith(
        "SearchIssues.Builder(jira, meter, SeededRandom()).actionType(SEARCH_JQL_CHANGELOG).jqlMemory(jqlMemory.changelog()).issueKeyMemory(issueKeyMemory).build()",
        "com.atlassian.performance.tools.jiraactions.api.SEARCH_JQL_CHANGELOG",
        "com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory.Companion.changelog",
        "com.atlassian.performance.tools.jiraactions.api.SeededRandom"
    )
)
class SearchJqlChangelogAction(
    jira: WebJira,
    meter: ActionMeter,
    jqlMemory: JqlMemory,
    issueKeyMemory: IssueKeyMemory
) : Action {

    private val action = SearchIssues.Builder(jira, meter, SeededRandom())
        .actionType(SEARCH_JQL_CHANGELOG)
        .jqlMemory(jqlMemory.changelog())
        .issueKeyMemory(issueKeyMemory)
        .build()

    override fun run() = action.run()
}
