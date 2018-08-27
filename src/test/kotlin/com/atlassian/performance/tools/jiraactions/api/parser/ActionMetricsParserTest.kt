package com.atlassian.performance.tools.jiraactions.api.parser

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.VIEW_BOARD
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class ActionMetricsParserTest {

    private val metricsParser = ActionMetricsParser()

    @Test
    fun shouldParse() {
        val metricsStream = javaClass.classLoader.getResourceAsStream("virtual-user-results/1/action-metrics.jpt")

        val metrics = metricsStream.use { metricsParser.parse(it) }

        val expectedMetric = ActionMetric(
            start = ZonedDateTime.of(
                2017,
                12,
                12,
                10,
                37,
                52,
                749000000,
                ZoneId.of("UTC")
            ).toInstant(),
            label = VIEW_BOARD.label,
            duration = Duration.ofMillis(982),
            virtualUser = UUID.fromString("0e5ead7c-dc9c-4f48-854d-5200a1a71058"),
            result = ActionResult.OK,
            observation = IssuesOnBoard(19).serialize()
        )
        assertThat(metrics, hasItem(expectedMetric))
    }
}