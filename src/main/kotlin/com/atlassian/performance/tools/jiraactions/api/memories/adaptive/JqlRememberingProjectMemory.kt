package com.atlassian.performance.tools.jiraactions.api.memories.adaptive

import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.Project
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql.ProjectJqlFactory
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql.SimpleProjectJqlFactory

class JqlRememberingProjectMemory private constructor(
    private val delegate: ProjectMemory,
    private val jqlMemory: JqlMemory,
    private val jqlFactory: ProjectJqlFactory
) : ProjectMemory by delegate {
    override fun remember(memories: Collection<Project>) = delegate.remember(memories)
        .also { jqlMemory.remember(memories.map { jqlFactory.createJql(it) }) }

    class Builder(
        private val delegate: ProjectMemory,
        private val jqlMemory: JqlMemory
    ) {
        private var jqlFactory: ProjectJqlFactory = SimpleProjectJqlFactory()

        fun queryResolver(jqlFactory: ProjectJqlFactory) = apply { this.jqlFactory = jqlFactory }

        fun build() = JqlRememberingProjectMemory(
            delegate = delegate,
            jqlMemory = jqlMemory,
            jqlFactory = jqlFactory
        )
    }
}
