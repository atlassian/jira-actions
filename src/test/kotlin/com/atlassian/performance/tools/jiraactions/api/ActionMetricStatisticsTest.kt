package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.api.ActionResult.ERROR
import com.atlassian.performance.tools.jiraactions.api.ActionResult.OK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Duration
import java.time.Duration.ofSeconds
import java.time.Instant.now


class ActionMetricStatisticsTest {

    private val actionMetrics = listOf(
        givenActionMetric("view", OK, ofSeconds(1)),
        givenActionMetric("view", OK, ofSeconds(2)),
        givenActionMetric("view", OK, ofSeconds(3)),
        givenActionMetric("view", ERROR, ofSeconds(4)),
        givenActionMetric("view", ERROR, ofSeconds(5)),
        givenActionMetric("create", ERROR, ofSeconds(1)),
        givenActionMetric("create", ERROR, ofSeconds(2)),
        givenActionMetric("create", ERROR, ofSeconds(3)),
        givenActionMetric("login", OK, ofSeconds(1))
    )

    private fun givenActionMetric(
        label: String,
        result: ActionResult,
        duration: Duration
    ): ActionMetric = ActionMetric.Builder(
        label = label,
        result = result,
        duration = duration,
        start = now()
    ).build()

    @Test
    fun testErrors() {
        val errors = ActionMetricStatistics(actionMetrics)
            .errors

        assertEquals(2, errors["view"])
        assertEquals(3, errors["create"])
        assertEquals(0, errors["login"])
    }

    @Test
    fun testSampleSize() {
        val sampleSize = ActionMetricStatistics(actionMetrics)
            .sampleSize

        assertEquals(3, sampleSize["view"])
        assertEquals(0, sampleSize["create"])
        assertEquals(1, sampleSize["login"])
    }

    @Test
    fun testPercentile() {
        val percentiles = ActionMetricStatistics(actionMetrics)
            .percentile(50)

        assertEquals(ofSeconds(2), percentiles["view"])
        assertNull(percentiles["create"])
        assertEquals(ofSeconds(1), percentiles["login"])
    }
}