package com.atlassian.performance.tools.jiraactions.action

import com.atlassian.performance.tools.jiraactions.BROWSE_PROJECTS
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.memories.ProjectMemory

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