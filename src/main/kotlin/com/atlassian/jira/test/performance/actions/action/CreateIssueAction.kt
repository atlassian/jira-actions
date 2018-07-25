package com.atlassian.jira.test.performance.actions.action

import com.atlassian.jira.test.performance.actions.*
import com.atlassian.jira.test.performance.actions.measure.ActionMeter
import com.atlassian.jira.test.performance.actions.memories.ProjectMemory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CreateIssueAction(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val projectMemory: ProjectMemory,
    private val seededRandom: SeededRandom
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val project = projectMemory.recall()
        if (project == null) {
            logger.info("Skipping Create issue action. I have no knowledge of projects.")
            return
        }
        meter.measure(CREATE_ISSUE) {
            val dashboardPage = meter.measure(VIEW_DASHBOARD) {
                jira.goToDashboard().waitForDashboard()
            }
            val issueCreateDialog = dashboardPage.openIssueCreateDialog()
            val filledForm = issueCreateDialog
                .waitForDialog()
                .selectProject(project.name)
                .selectIssueType(
                    seededRandom.pick(
                        issueCreateDialog.getIssueTypes().filter { it != "Epic" }
                    )!!
                )
                .fill("summary", "This is a simple summary")
                .fill("description", "And this is even simpler description")
            issueCreateDialog.fillRequiredFields()
            meter.measure(CREATE_ISSUE_SUBMIT) { filledForm.submit() }
        }
    }
}