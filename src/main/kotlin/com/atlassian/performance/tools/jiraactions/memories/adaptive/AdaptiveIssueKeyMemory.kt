package com.atlassian.performance.tools.jiraactions.memories.adaptive

import com.atlassian.performance.tools.jiraactions.SeededRandom
import com.atlassian.performance.tools.jiraactions.memories.IssueKeyMemory
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