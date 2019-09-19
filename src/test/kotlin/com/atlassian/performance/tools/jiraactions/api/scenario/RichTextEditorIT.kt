package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.dockerinfrastructure.api.browser.DockerisedChrome
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.JiraCoreFormula
import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.*
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory
import com.atlassian.performance.tools.jiraactions.api.w3c.DisabledW3cPerformanceTimeline
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Test
import java.nio.file.Paths
import java.time.Clock
import java.util.*

/**
 * Test Editing issue without SetupAction (i.e Rich Text editor enabled)
 * Covers:
 *      EditIssue
 *      AddComment
 */
class RichTextEditorIT {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    @After
    fun waitForCleanups() {
        Thread.sleep(10000)
    }

    @Test
    fun shouldRunScenarioWithoutErrors() {
        val version = System.getenv("JIRA_CORE_VERSION") ?: "8.0.0"
        logger.info("Testing Jira $version")
        val scenario = JiraEditScenario()
        val metrics = mutableListOf<ActionMetric>()
        val actionMeter = ActionMeter(
            virtualUser = UUID.randomUUID(),
            output = CollectionActionMetricOutput(metrics),
            clock = Clock.systemUTC(),
            w3cPerformanceTimeline = DisabledW3cPerformanceTimeline()
        )
        val user = User("admin", "admin")
        val userMemory = object : UserMemory {
            override fun recall(): User {
                return user
            }

            override fun remember(memories: Collection<User>) {
                throw Exception("not implemented")
            }
        }

        val diagnoses = Paths.get("diagnoses")
        JiraCoreFormula.Builder()
            .port(8082)
            .version(version)
            .diagnoses(diagnoses)
            .build()
            .provision()
            .use { jira ->
                DockerisedChrome(diagnoses.resolve("recordings"))
                    .start()
                    .use { browser ->
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

        val createIssue = CreateIssueAction(
            jira = jira,
            meter = meter,
            seededRandom = seededRandom,
            projectMemory = projectMemory
        )
        val searchWithJql = SearchJqlAction(
            jira = jira,
            meter = meter,
            jqlMemory = jqlMemory,
            issueKeyMemory = issueKeyMemory
        )
        val viewIssue = ViewIssueAction(
            jira = jira,
            meter = meter,
            issueKeyMemory = issueKeyMemory,
            issueMemory = issueMemory,
            jqlMemory = jqlMemory
        )
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
