package com.atlassian.performance.tools.jiraactions.api.parser

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.VIEW_BOARD
import com.atlassian.performance.tools.jiraactions.api.format.MetricCompactJsonFormat
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.Test
import java.time.Duration.ofMillis
import java.time.Duration.ofSeconds
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.streams.asSequence
import kotlin.streams.toList

class ActionMetricsParserTest {

    private val metricsParser = ActionMetricsParser()

    @Test
    fun shouldParse() {
        val metricsStream = javaClass.classLoader.getResourceAsStream("virtual-user-results/1/action-metrics.jpt")

        val metrics = metricsStream.use { metricsParser.stream(it).toList() }

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
        assertThat(metrics).contains(expectedMetric)
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

        SoftAssertions.assertSoftly {
            it.assertThat(drilldown.navigations[0].resource.entry.name)
                .isEqualTo("http://3.120.138.107:8080/issues/?jql=resolved+is+not+empty+order+by+description")
            it.assertThat(drilldown.navigations[0].resource.responseEnd)
                .isEqualTo(ofMillis(264))
            it.assertThat(drilldown.resources[9].initiatorType)
                .isEqualTo("xmlhttprequest")
            it.assertThat(drilldown.resources[28].requestStart)
                .isEqualTo(ofSeconds(1) + ofMillis(637))
        }
    }

    @Test
    fun shouldParseCompact() {
        val metricsStream = javaClass.classLoader.getResourceAsStream("virtual-user-results/1/action-metrics.jpt")

        val parser = ActionMetricsParser(MetricCompactJsonFormat())

        val metrics = metricsStream.use { parser.stream(it).toList() }

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
        assertThat(metrics).contains(expectedMetric)
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

        assertThat(fourthMetric.drilldown).isNull()
    }
}
