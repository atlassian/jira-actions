package com.atlassian.performance.tools.jiraactions.api.measure

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.ActionResult.ERROR
import com.atlassian.performance.tools.jiraactions.api.ActionResult.OK
import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.CREATE_ISSUE
import com.atlassian.performance.tools.jiraactions.api.EDIT_ISSUE
import com.atlassian.performance.tools.jiraactions.api.VIEW_BOARD
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.w3c.RecordedPerformanceEntries
import com.atlassian.performance.tools.jiraactions.api.w3c.W3cPerformanceTimeline
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Duration.ZERO
import java.time.Duration.ofSeconds
import java.time.Instant
import java.time.Instant.parse
import java.time.ZoneId
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate

class ActionMeterTest {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    private val start = parse("2007-12-03T10:15:29.00Z")
    private val vu = UUID.randomUUID()

    @Test
    fun shouldMeasureActions() {
        val tick = ofSeconds(1)
        val clock = TickingClock(start, tick)
        val output = CollectionActionMetricOutput(mutableListOf())
        val actionMeter = ActionMeter.Builder(
            output = output
        )
            .virtualUser(vu)
            .clock(clock)
            .build()

        actionMeter.measure(CREATE_ISSUE, clock::tick)
        actionMeter.measure(VIEW_BOARD) {}
        actionMeter.measure(EDIT_ISSUE, clock::tick)
        actionMeter.measure(CREATE_ISSUE) {}
        actionMeter.measure(VIEW_BOARD, clock::tick)
        actionMeter.measure(EDIT_ISSUE) {}

        val oneTickLater = start + tick
        val twoTicksLater = oneTickLater + tick
        val threeTicksLater = twoTicksLater + tick

        assertThat(output.metrics).containsExactlyInAnyOrder(
            expectedActionMetric(CREATE_ISSUE, OK, tick, start),
            expectedActionMetric(VIEW_BOARD, OK, ZERO, oneTickLater),
            expectedActionMetric(EDIT_ISSUE, OK, tick, oneTickLater),
            expectedActionMetric(CREATE_ISSUE, OK, ZERO, twoTicksLater),
            expectedActionMetric(VIEW_BOARD, OK, tick, twoTicksLater),
            expectedActionMetric(EDIT_ISSUE, OK, ZERO, threeTicksLater)
        )
    }

    private fun expectedActionMetric(
        actionType: ActionType<*>,
        result: ActionResult,
        duration: Duration,
        start: Instant
    ): ActionMetric = ActionMetric.Builder(
        label = actionType.label,
        result = result,
        duration = duration,
        start = start
    ).virtualUser(vu).build()

    @Test
    fun shouldMeasureErrors() {
        val output = CollectionActionMetricOutput(mutableListOf())
        val entries = RecordedPerformanceEntries(emptyList(), emptyList())
        val actionMeter = ActionMeter.Builder(
            output = output
        )
            .virtualUser(vu)
            .clock(Clock.fixed(start, ZoneId.of("UTC")))
            .performanceTimeline(HardcodedTimeline(entries))
            .build()

        try {
            actionMeter.measure(CREATE_ISSUE) { throw Exception("Ignore this exception, it's test-only") }
        } catch (e: Exception) {
            logger.info("Ignoring exception", e)
        }
        actionMeter.measure(VIEW_BOARD) {}

        assertThat(output.metrics).containsExactlyInAnyOrder(
            expectedActionMetric(CREATE_ISSUE, ERROR, ZERO, start),
            expectedActionMetric(VIEW_BOARD, OK, ZERO, start)
        )
        assertThat(output.metrics.first().drilldown)
            .`as`("drilldown for errored metric")
            .isNotNull
    }

    @Test
    fun shouldRespectDrilldownCondition() {
        val tick = ofSeconds(1)
        val clock = TickingClock(start, tick)
        val output = CollectionActionMetricOutput(mutableListOf())
        val w3cPerformanceTimelineMock = HardcodedTimeline(RecordedPerformanceEntries(emptyList(), emptyList()))
        val actionMeter = ActionMeter.Builder(
            output = output
        )
            .drilldownCondition(Predicate { actionMetric -> CREATE_ISSUE.label == actionMetric.label })
            .performanceTimeline(w3cPerformanceTimeline = w3cPerformanceTimelineMock)
            .virtualUser(vu)
            .clock(clock)
            .build()

        actionMeter.measure(CREATE_ISSUE, {})
        actionMeter.measure(VIEW_BOARD) {}
        actionMeter.measure(EDIT_ISSUE, {})
        actionMeter.measure(CREATE_ISSUE) {}
        actionMeter.measure(VIEW_BOARD, {})
        actionMeter.measure(EDIT_ISSUE) {}

        val actionsWithDrilldown = output.metrics.filter { it.drilldown != null }.map { it.label }.toSet()
        assertThat(actionsWithDrilldown).containsOnly(
            CREATE_ISSUE.label
        )
    }

    @Test
    fun shouldNotMeasureObservation() {
        val tick = ofSeconds(1)
        val clock = TickingClock(start, tick)
        val output = CollectionActionMetricOutput(mutableListOf())
        val actionMeter = ActionMeter.Builder(
            output = output
        )
            .virtualUser(vu)
            .clock(clock)
            .build()

        actionMeter.measure(
            key = EDIT_ISSUE,
            action = {},
            observation = {
                clock.tick()
                null
            }
        )

        assertThat(output.metrics).containsExactlyInAnyOrder(
            expectedActionMetric(EDIT_ISSUE, OK, ZERO, start)
        )
    }

    @Test
    fun shouldHookAction() {
        val output = CollectionActionMetricOutput(mutableListOf())
        val hook = CountingHook()
        val tick = ofSeconds(1)
        val clock = TickingClock(start, tick)
        val actionMeter = ActionMeter.Builder(
            output = output
        )
            .virtualUser(vu)
            .clock(clock)
            .postMetricHook(hook)
            .build()

        actionMeter.measure(CREATE_ISSUE, clock::tick)
        actionMeter.measure(VIEW_BOARD, clock::tick)

        assertThat(hook.getCount()).isEqualTo(2)
        assertThat(output.metrics.map { it.duration }.toSet()).containsOnly(ofSeconds(1))
    }

    private class CountingHook : PostMetricHook {
        private val counter = AtomicInteger()
        override fun run(actionMetricBuilder : ActionMetric.Builder) {
            counter.incrementAndGet()
        }

        fun getCount(): Int {
            return counter.get()
        }

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

class HardcodedTimeline(
    private val entries: RecordedPerformanceEntries
) : W3cPerformanceTimeline {

    override fun record(): RecordedPerformanceEntries? = entries
}
