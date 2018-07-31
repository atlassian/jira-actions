package com.atlassian.performance.tools.jiraactions.action

import com.atlassian.performance.tools.jiraactions.PROJECT_SUMMARY
import com.atlassian.performance.tools.jiraactions.WebJira
import com.atlassian.performance.tools.jiraactions.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.memories.ProjectMemory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ProjectSummaryAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val projectMemory: ProjectMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val project = projectMemory.recall()
        if (project == null) {
            logger.info("Skipping Project summary action. I have no knowledge of projects.")
            return
        }
        meter.measure(PROJECT_SUMMARY) {
            jira.goToProjectSummary(project.key).waitForMetadata()
        }
    }
}