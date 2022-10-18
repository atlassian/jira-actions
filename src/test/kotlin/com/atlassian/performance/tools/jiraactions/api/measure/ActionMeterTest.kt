package com.atlassian.performance.tools.jiraactions.api.measure

import com.atlassian.performance.tools.jiraactions.api.*
import com.atlassian.performance.tools.jiraactions.api.ActionResult.ERROR
import com.atlassian.performance.tools.jiraactions.api.ActionResult.OK
import com.atlassian.performance.tools.jiraactions.api.measure.output.ActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.measure.output.CollectionActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.w3c.RecordedPerformanceEntries
import com.atlassian.performance.tools.jiraactions.api.w3c.W3cPerformanceTimeline
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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

    private class TestException : Exception()

    @Test
    fun shouldMeasureErrors() {
        val output = CollectionActionMetricOutput(mutableListOf())
        val entries = RecordedPerformanceEntries(emptyList(), emptyList(), emptyList())
        val actionMeter = ActionMeter.Builder(
            output = output
        )
            .virtualUser(vu)
            .clock(Clock.fixed(start, ZoneId.of("UTC")))
            .appendPostMetricHook(DrillDownHook(HardcodedTimeline(entries)))
            .build()

        assertThatThrownBy { actionMeter.measure(CREATE_ISSUE) { throw TestException() } }.hasCauseInstanceOf(TestException::class.java)
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
        val w3cPerformanceTimelineMock = HardcodedTimeline(RecordedPerformanceEntries(emptyList(), emptyList(), emptyList()))
        val actionMeter = ActionMeter.Builder(
            output = output
        )
            .appendPostMetricHook(
                ConditionalHook(
                    Predicate { actionMetric -> CREATE_ISSUE.label == actionMetric.label },
                    DrillDownHook(w3cPerformanceTimelineMock)
                )
            )
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

    private class ConditionalHook(
        private val predicate: Predicate<ActionMetric>,
        private val hook: PostMetricHook
    ) : PostMetricHook {
        override fun run(actionMetricBuilder: ActionMetric.Builder) {
            if (predicate.test(actionMetricBuilder.build())) {
                hook.run(actionMetricBuilder)
            }
        }
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
        val hook1 = CountingHook()
        val hook2 = CountingHook()
        val hook3 = CountingHook()
        val tick = ofSeconds(1)
        val clock = TickingClock(start, tick)
        val actionMeter = ActionMeter.Builder(
            output = output
        )
            .virtualUser(vu)
            .clock(clock)
            .appendPostMetricHook(hook1)
            .appendPostMetricHook(hook2)
            .appendPostMetricHook(hook3)
            .build()

        actionMeter.measure(CREATE_ISSUE, clock::tick)
        actionMeter.measure(VIEW_BOARD, clock::tick)

        assertThat(hook1.getCount()).isEqualTo(2)
        assertThat(hook2.getCount()).isEqualTo(2)
        assertThat(hook3.getCount()).isEqualTo(2)
        assertThat(output.metrics.map { it.duration }.toSet()).containsOnly(ofSeconds(1))
    }

    @Test
    fun shouldOverrideOutput() {
        val output = CollectionActionMetricOutput(mutableListOf())
        val actionMeter = ActionMeter.Builder(
            ActionMeter.Builder(
                output = ThrowingActionMetricOutput()
            )
                .virtualUser(vu)
                .build()
        )
            .overrideOutput { output }
            .build()

        actionMeter.measure(CREATE_ISSUE) {}
        actionMeter.measure(VIEW_BOARD) {}

        assertThat(output.metrics).hasSize(2)
    }

    private class ThrowingActionMetricOutput : ActionMetricOutput {
        override fun write(metric: ActionMetric) {
            throw Exception()
        }
    }

    @Test
    fun shouldDelegateOutput() {
        val output = CollectionActionMetricOutput(mutableListOf())
        val actionMeter = ActionMeter.Builder(
            ActionMeter.Builder(
                output = output
            )
                .virtualUser(vu)
                .build()
        )
            .overrideOutput { DelegatingOutput(it) }
            .build()

        actionMeter.measure(CREATE_ISSUE) {}
        actionMeter.measure(VIEW_BOARD) {}

        assertThat(output.metrics).hasSize(2)
    }

    private class DelegatingOutput(
        private val delegate: ActionMetricOutput
    ) : ActionMetricOutput {
        override fun write(metric: ActionMetric) {
            delegate.write(metric)
        }
    }

    private class CountingHook : PostMetricHook {
        private val counter = AtomicInteger()
        override fun run(actionMetricBuilder: ActionMetric.Builder) {
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
