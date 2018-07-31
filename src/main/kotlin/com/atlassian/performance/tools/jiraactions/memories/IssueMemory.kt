package com.atlassian.performance.tools.jiraactions.memories

interface IssueMemory {
    fun recall(): Issue?
    fun recall(filter: (Issue) -> Boolean): Issue?
    fun remember(issues: Collection<Issue>)
}