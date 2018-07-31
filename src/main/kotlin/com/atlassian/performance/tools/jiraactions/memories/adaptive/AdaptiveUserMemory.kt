package com.atlassian.performance.tools.jiraactions.memories.adaptive

import com.atlassian.performance.tools.jiraactions.SeededRandom
import com.atlassian.performance.tools.jiraactions.memories.User
import com.atlassian.performance.tools.jiraactions.memories.UserMemory

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