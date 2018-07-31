package com.atlassian.performance.tools.jiraactions.memories

interface Memory<T> {
    fun recall(): T?
    fun remember(memories: Collection<T>)
}