package com.atlassian.performance.tools.jiraactions.api.scenario

fun <T> MutableList<T>.addMultiple(
    repeats: Int,
    element: T
) {
    repeat(repeats, { this.add(element) })
}