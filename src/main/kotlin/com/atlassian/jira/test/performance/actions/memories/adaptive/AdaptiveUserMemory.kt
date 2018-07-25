package com.atlassian.jira.test.performance.actions.memories.adaptive

import com.atlassian.jira.test.performance.actions.SeededRandom
import com.atlassian.jira.test.performance.actions.memories.User
import com.atlassian.jira.test.performance.actions.memories.UserMemory

class AdaptiveUserMemory(
    private val random: SeededRandom
) : UserMemory {
    private val users = mutableSetOf<User>()

    override fun recall(): User? {
        if (users.isEmpty()) {
            return null
        }
        return random.pick(users.toList())
    }

    override fun remember(memories: Collection<User>) {
        users.addAll(memories)
    }
}