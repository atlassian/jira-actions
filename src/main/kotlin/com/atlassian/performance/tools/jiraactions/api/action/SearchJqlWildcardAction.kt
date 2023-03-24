package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL_WILDCARD
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.jql.BuiltInJQL
import com.atlassian.performance.tools.jiraactions.memories.jql.TagSelectiveJqlMemory
import java.util.function.Predicate

@Deprecated("Use SearchIssues.Builder")
class SearchJqlWildcardAction(
    jira: WebJira,
    meter: ActionMeter,
    jqlMemory: JqlMemory,
    issueKeyMemory: IssueKeyMemory
) : Action {

    private val action = SearchIssues.Builder(jira, meter, SeededRandom())
        .actionType(SEARCH_WITH_JQL_WILDCARD)
        .jqlMemory(TagSelectiveJqlMemory(jqlMemory, Predicate { it == BuiltInJQL.GENERIC_WIDE.name }))
        .issueKeyMemory(issueKeyMemory)
        .build()

    override fun run() = action.run()
}
