package com.atlassian.performance.tools.jiraactions.api.memories.adaptive

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory

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