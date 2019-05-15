package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.dockerinfrastructure.api.browser.DockerisedChrome
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.JiraCoreFormula
import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.VIEW_ISSUE
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.w3c.DisabledW3cPerformanceTimeline
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions
import org.junit.Test
import java.time.Clock
import java.util.UUID

class JiraCoreScenarioIT {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    @Test
    fun shouldRunScenarioWithoutErrors() {
        val version = System.getenv("JIRA_SOFTWARE_VERSION") ?: "8.0.0"
        logger.info("Testing Jira $version")
        val scenario = JiraCoreScenario()
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

        JiraCoreFormula.Builder()
            .version(version)
            .build()
            .provision()
            .use { jira ->
                DockerisedChrome().start().use { browser ->
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
                    val setupAction = scenario.getSetupAction(
                        webJira,
                        actionMeter
                    )
                    val actions = scenario.getActions(
                        webJira,
                        SeededRandom(123),
                        actionMeter
                    )

                    logInAction.run()
                    setupAction.run()
                    actions.forEach { action ->
                        action.run()
                    }
                }
            }

        val results = metrics.map { metric ->
            metric.result
        }
        Assertions.assertThat(results).containsOnly(ActionResult.OK)
        val viewIssueMetrics = metrics.filter {
            VIEW_ISSUE.label.equals(it.label)
        }
        Assertions.assertThat(viewIssueMetrics).allMatch { m -> m.observation != null }
    }
}