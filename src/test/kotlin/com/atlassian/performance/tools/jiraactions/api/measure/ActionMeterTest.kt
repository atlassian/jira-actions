package com.atlassian.performance.tools.jiraactions.api.measure

import com.atlassian.performance.tools.jiraactions.api.*
import com.atlassian.performance.tools.jiraactions.api.ActionResult.ERROR
import com.atlassian.performance.tools.jiraactions.api.ActionResult.OK
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.w3c.DisabledW3cPerformanceTimeline
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
            clock = clock,
            w3cPerformanceTimeline = DisabledW3cPerformanceTimeline()
        )

        actionMeter.measure(CREATE_ISSUE, clock::tick)
        actionMeter.measure(VIEW_BOARD) {}
        actionMeter.measure(EDIT_ISSUE, clock::tick)
        actionMeter.measure(CREATE_ISSUE) {}
        actionMeter.measure(VIEW_BOARD, clock::tick)
        actionMeter.measure(EDIT_ISSUE) {}

        val oneTickLater = start + tick
        val twoTicksLater = oneTickLater + tick
        val threeTicksLater = twoTicksLater + tick
        assertThat(
            output.metrics,
            containsInAnyOrder(
                expectedActionMetric(CREATE_ISSUE, OK, tick, start),
                expectedActionMetric(VIEW_BOARD, OK, ZERO, oneTickLater),
                expectedActionMetric(EDIT_ISSUE, OK, tick, oneTickLater),
                expectedActionMetric(CREATE_ISSUE, OK, ZERO, twoTicksLater),
                expectedActionMetric(VIEW_BOARD, OK, tick, twoTicksLater),
                expectedActionMetric(EDIT_ISSUE, OK, ZERO, threeTicksLater)
            )
        )
    }

    private fun expectedActionMetric(
        actionType: ActionType<*>,
        result: ActionResult,
        duration: Duration,
        start: Instant
    ): ActionMetric = ActionMetric(
        label = actionType.label,
        result = result,
        duration = duration,
        start = start,
        virtualUser = vu,
        observation = null,
        drilldown = null
    )

    @Test
    fun shouldMeasureErrors() {
        val output = CollectionActionMetricOutput(mutableListOf())
        val actionMeter = ActionMeter(
            output = output,
            virtualUser = vu,
            clock = Clock.fixed(start, ZoneId.of("UTC")),
            w3cPerformanceTimeline = DisabledW3cPerformanceTimeline()
        )

        try {
            actionMeter.measure(CREATE_ISSUE) { throw Exception("oops") }
        } catch (e: Exception) {
            logger.info("Ignoring exception", e)
        }
        actionMeter.measure(VIEW_BOARD) {}

        assertThat(
            output.metrics,
            containsInAnyOrder(
                expectedActionMetric(CREATE_ISSUE, ERROR, ZERO, start),
                expectedActionMetric(VIEW_BOARD, OK, ZERO, start)
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
