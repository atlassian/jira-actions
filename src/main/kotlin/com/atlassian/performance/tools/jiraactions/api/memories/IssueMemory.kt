package com.atlassian.performance.tools.jiraactions.api.memories

interface IssueMemory {
    fun recall(): Issue?
    fun recall(filter: (Issue) -> Boolean): Issue?
    fun remember(issues: Collection<Issue>)
}