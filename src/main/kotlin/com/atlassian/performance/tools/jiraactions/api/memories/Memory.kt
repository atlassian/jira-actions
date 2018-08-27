package com.atlassian.performance.tools.jiraactions.api.memories

interface Memory<T> {
    fun recall(): T?
    fun remember(memories: Collection<T>)
}