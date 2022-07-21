package com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql

import com.atlassian.performance.tools.jiraactions.api.memories.Project

interface ProjectJqlFactory {
    fun createJql(project: Project): String
}
