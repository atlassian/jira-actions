package com.atlassian.jira.test.performance.actions.memories

interface Memory<T> {
    fun recall(): T?
    fun remember(memories: Collection<T>)
}