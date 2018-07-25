package com.atlassian.jira.test.performance.actions.memories.adaptive

import com.atlassian.jira.test.performance.actions.SeededRandom
import com.atlassian.jira.test.performance.actions.memories.Project
import com.atlassian.jira.test.performance.actions.memories.ProjectMemory

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