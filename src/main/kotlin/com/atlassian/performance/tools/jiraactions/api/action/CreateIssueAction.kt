package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.api.*
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
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
            logger.debug("Skipping Create issue action. I have no knowledge of projects.")
            return
        }

        val topNav = jira.getTopNav()
        if (!topNav.isPresent() || topNav.isObscured()) {
            meter.measure(VIEW_DASHBOARD) {
                jira.goToDashboard().waitForDashboard()
            }.apply {
                dismissAllPopups()
            }
        }

        meter.measure(CREATE_ISSUE) {
            val issueCreateDialog = topNav.openIssueCreateDialog()
            val filledForm = issueCreateDialog
                .waitForDialog()
                .showAllFields()
                .selectProject(project.name)
                .selectIssueType {
                    issueTypes -> seededRandom.pick(issueTypes.filter { it != "Epic" } )!!
                }.fill("summary", "This is a simple summary")
            issueCreateDialog.fillRequiredFields()
            meter.measure(CREATE_ISSUE_SUBMIT) { filledForm.submit() }
        }
    }
}
