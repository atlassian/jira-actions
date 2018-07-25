package com.atlassian.jira.test.performance.measure

import com.atlassian.jira.test.performance.actions.ActionMetric
import com.atlassian.jira.test.performance.actions.ActionResult.ERROR
import com.atlassian.jira.test.performance.actions.ActionResult.OK
import com.atlassian.jira.test.performance.actions.CREATE_ISSUE
import com.atlassian.jira.test.performance.actions.EDIT_ISSUE
import com.atlassian.jira.test.performance.actions.VIEW_BOARD
import com.atlassian.jira.test.performance.actions.measure.ActionMeter
import com.atlassian.jira.test.performance.actions.measure.output.CollectionActionMetricOutput
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Duration.ZERO
import java.time.Duration.ofSeconds
import java.time.Instant
import java.time.Instant.parse
import java.time.ZoneId
import java.util.*

class ActionMeterTest {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    private val start = parse("2007-12-03T10:15:29.00Z")
    private val vu = UUID.randomUUID()

    @Test
    fun shouldMeasureActions() {
        val tick = ofSeconds(1)
        val clock = TickingClock(start, tick)
        val output = CollectionActionMetricOutput(mutableListOf())
        val actionMeter = ActionMeter(
            virtualUser = vu,
            output = output,
            clock = clock
        )

        actionMeter.measure(CREATE_ISSUE, clock::tick)
        actionMeter.measure(VIEW_BOARD, {})
        actionMeter.measure(EDIT_ISSUE, clock::tick)
        actionMeter.measure(CREATE_ISSUE, {})
        actionMeter.measure(VIEW_BOARD, clock::tick)
        actionMeter.measure(EDIT_ISSUE, {})

        val oneTickLater = start + tick
        val twoTicksLater = oneTickLater + tick
        val threeTicksLater = twoTicksLater + tick
        assertThat(
            output.metrics,
            containsInAnyOrder(
                ActionMetric(CREATE_ISSUE.label, OK, tick, start, vu),
                ActionMetric(VIEW_BOARD.label, OK, ZERO, oneTickLater, vu),
                ActionMetric(EDIT_ISSUE.label, OK, tick, oneTickLater, vu),
                ActionMetric(CREATE_ISSUE.label, OK, ZERO, twoTicksLater, vu),
                ActionMetric(VIEW_BOARD.label, OK, tick, twoTicksLater, vu),
                ActionMetric(EDIT_ISSUE.label, OK, ZERO, threeTicksLater, vu)
            )
        )
    }

    @Test
    fun shouldMeasureErrors() {
        val output = CollectionActionMetricOutput(mutableListOf())
        val actionMeter = ActionMeter(
            output = output,
            virtualUser = vu,
            clock = Clock.fixed(start, ZoneId.of("UTC"))
        )

        try {
            actionMeter.measure(CREATE_ISSUE, { throw Exception("oops") })
        } catch (e: Exception) {
            logger.info("Ignoring exception", e)
        }
        actionMeter.measure(VIEW_BOARD, {})

        assertThat(
            output.metrics,
            containsInAnyOrder(
                ActionMetric(CREATE_ISSUE.label, ERROR, ZERO, start, vu),
                ActionMetric(VIEW_BOARD.label, OK, ZERO, start, vu)
            )
        )
    }

    class TickingClock(
        private var now: Instant,
        private val tick: Duration
    ) : Clock() {

        fun tick() {
            now += tick
        }

        override fun instant(): Instant = now
        override fun withZone(zone: ZoneId?): Clock = throw Exception("We didn't expect a timezone would be needed")
        override fun getZone(): ZoneId = throw Exception("We didn't expect a timezone would be needed")
    }
}