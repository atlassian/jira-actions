package com.atlassian.performance.tools.jiraactions.api.parser

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.VIEW_BOARD
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import com.atlassian.performance.tools.jiraactions.api.format.MetricCompactJsonFormat
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Duration.ofMillis
import java.time.Duration.ofSeconds
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.streams.asSequence

class ActionMetricsParserTest {

    private val metricsParser = ActionMetricsParser()

    @Test
    fun shouldParse() {
        val metricsStream = javaClass.classLoader.getResourceAsStream("virtual-user-results/1/action-metrics.jpt")

        @Suppress("Deprecation")
        val metrics = metricsStream.use { metricsParser.parse(it) }

        val expectedMetric = ActionMetric.Builder(
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
            duration = ofMillis(982),
            result = ActionResult.OK
        )
            .virtualUser(UUID.fromString("0e5ead7c-dc9c-4f48-854d-5200a1a71058"))
            .observation(IssuesOnBoard(19).serialize())
            .build()
        assertThat(metrics, hasItem(expectedMetric))
    }

    @Test
    fun shouldStreamWithDrilldown() {
        val metricsStream = javaClass.classLoader.getResourceAsStream("virtual-user-results/2/action-metrics.jpt")

        val fourthMetric = metricsStream.use {
            metricsParser
                .stream(it)
                .asSequence()
                .drop(3)
                .first()
        }

        val drilldown = fourthMetric.drilldown!!
        assertThat(
            drilldown.navigations[0].resource.entry.name,
            equalTo("http://3.120.138.107:8080/issues/?jql=resolved+is+not+empty+order+by+description")
        )
        assertThat(
            drilldown.navigations[0].resource.responseEnd,
            equalTo(ofMillis(264))
        )
        assertThat(
            drilldown.resources[9].initiatorType,
            equalTo("xmlhttprequest")
        )
        assertThat(
            drilldown.resources[28].requestStart,
            equalTo(ofSeconds(1) + ofMillis(637))
        )
    }

    @Test
    fun shouldParseCompact() {
        val metricsStream = javaClass.classLoader.getResourceAsStream("virtual-user-results/1/action-metrics.jpt")

        val parser = ActionMetricsParser(MetricCompactJsonFormat())

        @Suppress("Deprecation")
        val metrics = metricsStream.use { parser.parse(it) }

        val expectedMetric = ActionMetric.Builder(
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
            duration = ofMillis(982),
            result = ActionResult.OK
        )
            .virtualUser(UUID.fromString("0e5ead7c-dc9c-4f48-854d-5200a1a71058"))
            .observation(IssuesOnBoard(19).serialize())
            .build()
        assertThat(metrics, hasItem(expectedMetric))
    }

    @Test
    fun shouldStreamWithoutDrilldown() {
        val metricsStream = javaClass.classLoader.getResourceAsStream("virtual-user-results/2/action-metrics.jpt")

        val parser = ActionMetricsParser(MetricCompactJsonFormat())

        val fourthMetric = metricsStream.use {
            parser
                .stream(it)
                .asSequence()
                .drop(3)
                .first()
        }

        assertNull(fourthMetric.drilldown)
    }
}
