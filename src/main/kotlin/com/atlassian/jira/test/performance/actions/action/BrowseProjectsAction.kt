package com.atlassian.jira.test.performance.actions.action

import com.atlassian.jira.test.performance.actions.BROWSE_PROJECTS
import com.atlassian.jira.test.performance.actions.WebJira
import com.atlassian.jira.test.performance.actions.action.Action
import com.atlassian.jira.test.performance.actions.measure.ActionMeter
import com.atlassian.jira.test.performance.actions.memories.ProjectMemory

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