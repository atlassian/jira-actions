package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.api.ActionResult.ERROR
import com.atlassian.performance.tools.jiraactions.api.ActionResult.OK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Duration.ofSeconds
import java.time.Instant.now
import java.util.UUID.randomUUID


class ActionMetricStatisticsTest {

    private val actionMetrics = listOf(
        ActionMetric("view", OK, ofSeconds(1), now(), randomUUID(), null),
        ActionMetric("view", OK, ofSeconds(2), now(), randomUUID(), null),
        ActionMetric("view", OK, ofSeconds(3), now(), randomUUID(), null),
        ActionMetric("view", ERROR, ofSeconds(4), now(), randomUUID(), null),
        ActionMetric("view", ERROR, ofSeconds(5), now(), randomUUID(), null),
        ActionMetric("create", ERROR, ofSeconds(1), now(), randomUUID(), null),
        ActionMetric("create", ERROR, ofSeconds(2), now(), randomUUID(), null),
        ActionMetric("create", ERROR, ofSeconds(3), now(), randomUUID(), null),
        ActionMetric("login", OK, ofSeconds(1), now(), randomUUID(), null)
    )

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