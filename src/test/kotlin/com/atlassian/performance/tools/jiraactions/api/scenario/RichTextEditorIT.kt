package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.dockerinfrastructure.api.browser.DockerisedChrome
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.JiraCoreFormula
import com.atlassian.performance.tools.jiraactions.api.*
import com.atlassian.performance.tools.jiraactions.api.action.*
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions
import org.junit.Test
import java.nio.file.Paths

/**
 * Test Editing issue without SetupAction (i.e Rich Text editor enabled)
 * Covers:
 *      EditIssue
 *      AddComment
 */
class RichTextEditorIT {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    @Test
    fun shouldRunScenarioWithoutErrors() {
        val version = System.getenv("JIRA_SOFTWARE_VERSION") ?: "8.0.0"
        logger.info("Testing Jira $version")
        val scenario = JiraEditScenario()
        val metrics = mutableListOf<ActionMetric>()
        val actionMeter = ActionMeter.Builder(
            output = CollectionActionMetricOutput(metrics)
        ).build()

        val user = User("admin", "admin")
        val userMemory = object : UserMemory {
            override fun recall(): User {
                return user
            }

            override fun remember(memories: Collection<User>) {
                throw Exception("not implemented")
            }
        }

        JiraCoreFormula.Builder()
            .version(version)
            .build()
            .provision()
            .use { jira ->
                val recordings = Paths.get("build/diagnoses/recordings/" + this::class.java.simpleName)
                DockerisedChrome(recordings).start().use { browser ->
                    val webJira = WebJira(
                        browser.driver,
                        jira.getUri(),
                        user.password
                    )
                    val logInAction = scenario.getLogInAction(
                        webJira,
                        actionMeter,
                        userMemory
                    )
                    val actions = scenario.getActions(
                        webJira,
                        SeededRandom(123),
                        actionMeter
                    )

                    logInAction.run()
                    actions.forEach { action ->
                        action.run()
                    }
                }
            }

        val results = metrics.map { metric ->
            metric.result
        }
        Assertions.assertThat(results).containsOnly(ActionResult.OK)

    }
}

class JiraEditScenario : Scenario {

    override fun getActions(jira: WebJira, seededRandom: SeededRandom, meter: ActionMeter): List<Action> {
        val projectMemory = AdaptiveProjectMemory(random = seededRandom)
        val jqlMemory = AdaptiveJqlMemory(seededRandom)
        val issueKeyMemory = AdaptiveIssueKeyMemory(random = seededRandom)
        val issueMemory = AdaptiveIssueMemory(issueKeyMemory, seededRandom)
        val commentMemory = AdaptiveCommentMemory(seededRandom)

        val createIssue = CreateIssueAction(
            jira = jira,
            meter = meter,
            seededRandom = seededRandom,
            projectMemory = projectMemory
        )
        val searchWithJql = SearchIssues.Builder(jira, meter, SeededRandom())
            .actionType(SEARCH_WITH_JQL)
            .jqlMemory(jqlMemory)
            .issueKeyMemory(issueKeyMemory)
            .build()

        val viewIssue = ViewIssueAction.Builder(jira, meter)
            .issueKeyMemory(issueKeyMemory)
            .issueMemory(issueMemory)
            .jqlMemory(jqlMemory)
            .commentMemory(commentMemory)
            .build()

        val editIssue = EditIssueAction(
            jira = jira,
            meter = meter,
            issueMemory = issueMemory
        )
        val addComment = AddCommentAction(
            jira = jira,
            meter = meter,
            issueMemory = issueMemory
        )

        return mutableListOf(
            createIssue,
            searchWithJql,
            viewIssue,
            editIssue,
            addComment
        )
    }

}
