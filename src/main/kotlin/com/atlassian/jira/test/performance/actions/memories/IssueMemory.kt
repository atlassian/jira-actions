package com.atlassian.jira.test.performance.actions.memories

interface IssueMemory {
    fun recall(): Issue?
    fun recall(filter: (Issue) -> Boolean): Issue?
    fun remember(issues: Collection<Issue>)
}