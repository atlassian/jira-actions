package com.atlassian.jira.test.performance.actions.action

import com.atlassian.jira.test.performance.actions.PROJECT_SUMMARY
import com.atlassian.jira.test.performance.actions.WebJira
import com.atlassian.jira.test.performance.actions.measure.ActionMeter
import com.atlassian.jira.test.performance.actions.memories.ProjectMemory
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