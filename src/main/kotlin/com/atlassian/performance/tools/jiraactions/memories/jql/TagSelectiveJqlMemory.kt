package com.atlassian.performance.tools.jiraactions.memories.jql

import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import java.util.function.Predicate

/**
 * Only [recall]s so called "tagged" JQLs matching the filter.
 */
internal class TagSelectiveJqlMemory(
    private val memory: JqlMemory,
    private val jqlTagFilter: Predicate<String>
) : JqlMemory by memory {

    override fun recall() = memory.recallByTag(jqlTagFilter)
}
