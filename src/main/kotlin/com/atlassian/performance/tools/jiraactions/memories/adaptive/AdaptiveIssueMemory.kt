package com.atlassian.performance.tools.jiraactions.memories.adaptive

import com.atlassian.performance.tools.jiraactions.SeededRandom
import com.atlassian.performance.tools.jiraactions.memories.Issue
import com.atlassian.performance.tools.jiraactions.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.memories.IssueMemory
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class AdaptiveIssueMemory(
    private val issueKeyMemory: IssueKeyMemory,
    private val random: SeededRandom
) : IssueMemory {

    private val issues = mutableSetOf<Issue>()

    override fun recall(): Issue? {
        return random.pick(issues.toList())
    }

    override fun recall(filter: (Issue) -> Boolean): Issue? {
        return random.pick(issues.filter(filter))
    }

    override fun remember(issues: Collection<Issue>) {
        this.issues.addAll(issues)
        issueKeyMemory.remember(issues.map { it.key })
    }
}