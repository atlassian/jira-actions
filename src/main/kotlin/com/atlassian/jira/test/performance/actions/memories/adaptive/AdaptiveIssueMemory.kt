package com.atlassian.jira.test.performance.actions.memories.adaptive

import com.atlassian.jira.test.performance.actions.SeededRandom
import com.atlassian.jira.test.performance.actions.memories.Issue
import com.atlassian.jira.test.performance.actions.memories.IssueKeyMemory
import com.atlassian.jira.test.performance.actions.memories.IssueMemory
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