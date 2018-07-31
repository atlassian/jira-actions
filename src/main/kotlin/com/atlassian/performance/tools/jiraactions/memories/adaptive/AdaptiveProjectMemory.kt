package com.atlassian.performance.tools.jiraactions.memories.adaptive

import com.atlassian.performance.tools.jiraactions.SeededRandom
import com.atlassian.performance.tools.jiraactions.memories.Project
import com.atlassian.performance.tools.jiraactions.memories.ProjectMemory

class AdaptiveProjectMemory(
    private val random: SeededRandom
) : ProjectMemory {
    private val projects = mutableSetOf<Project>()

    override fun recall(): Project? {
        if (projects.isEmpty()) {
            return null
        }
        return random.pick(projects.toList())
    }

    override fun remember(memories: Collection<Project>) {
        projects.addAll(memories)
    }
}