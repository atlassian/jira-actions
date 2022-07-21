package com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql

import com.atlassian.performance.tools.jiraactions.api.memories.Project

class SimpleProjectJqlFactory : ProjectJqlFactory {
    override fun createJql(project: Project) = "project = ${project.key} order by issueKey desc"
}
