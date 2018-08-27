package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.BROWSE_PROJECTS
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory

class BrowseProjectsAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val projectMemory: ProjectMemory
) : Action {
    var page = 1

    override fun run() {
        val browseProjectsPage =
            meter.measure(BROWSE_PROJECTS) { jira.goToBrowseProjects(page).waitForProjectList() }

        page = browseProjectsPage.getNextPage() ?: 1

        projectMemory.remember(browseProjectsPage.getProjects())
    }
}