package com.atlassian.jira.test.performance.actions.memories.adaptive

import com.atlassian.jira.test.performance.actions.SeededRandom
import com.atlassian.jira.test.performance.actions.memories.IssueKeyMemory
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class AdaptiveIssueKeyMemory(
    private val random: SeededRandom
) : IssueKeyMemory {
    private val issues = mutableSetOf<String>()
    override fun recall(): String? {
        if (issues.isEmpty()) {
            return null
        }
        return random.pick(issues.toList())
    }

    override fun remember(memories: Collection<String>) {
        issues.addAll(memories)
    }
}