package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.dockerinfrastructure.api.browser.DockerisedChrome
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.JiraCoreFormula
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.measure.output.ThrowawayActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.memories.Project
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import org.junit.Test
import java.nio.file.Paths
import java.util.*

class CreateIssueActionIT {
    @Test
    fun shouldRecoverFromObscuredCreateIssueButton() {
        JiraCoreFormula.Builder()
            .version("9.8.0")
            .build()
            .provision().use { jira ->
                val recordings = Paths.get("build/diagnoses/recordings/")
                    .resolve(this::class.java.simpleName)
                    .resolve("seed-${UUID.randomUUID()}")
                DockerisedChrome(recordings).start().use { browser ->
                    val webJira = WebJira(
                        browser.driver,
                        jira.getUri(),
                        "admin"
                    )
                    LogInAction(webJira, actionMeter(), userMemory()).run()

                    obscureCreateIssueButton(webJira)
                    CreateIssueAction(webJira, actionMeter(), projectMemory(), SeededRandom()).run()
                }
            }
    }

    private fun userMemory() = object : UserMemory {
        override fun recall() = User("admin", "admin")
        override fun remember(memories: Collection<User>) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Opened issue create dialog obscures the top nav, so also obscures create issue button
     */
    private fun obscureCreateIssueButton(webJira: WebJira) {
        webJira
            .also { it.goToDashboard().waitForDashboard() }
            .getTopNav()
            .openIssueCreateDialog()
    }

    private fun actionMeter() = ActionMeter.Builder(ThrowawayActionMetricOutput()).build()

    private fun projectMemory() = object : ProjectMemory {
        override fun recall() = Project(key = "SAM", name = "Sample")
        override fun remember(memories: Collection<Project>) {
            TODO("Not yet implemented")
        }
    }
}
