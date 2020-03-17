package com.atlassian.performance.tools.jiraactions.api.measure

import com.atlassian.performance.tools.concurrency.api.representsInterrupt
import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.measure.output.ActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.measure.output.ThrowawayActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.w3c.DisabledW3cPerformanceTimeline
import com.atlassian.performance.tools.jiraactions.api.w3c.RecordedPerformanceEntries
import com.atlassian.performance.tools.jiraactions.api.w3c.W3cPerformanceTimeline
import java.time.Clock
import java.time.Duration
import java.util.*
import java.util.function.Predicate
import javax.json.JsonObject

/**
 * Records multiple measurements and observations of various actions.
 *
 * @param virtualUser identifies a virtual user
 * @param output writes the metrics
 * @param clock measures latencies
 * @param w3cPerformanceTimeline records W3C performance entries
 * @param drilldownCondition condition based on ActionMetric upon which drilldown is performed
 */
class ActionMeter private constructor(
    private val virtualUser: UUID,
    private val output: ActionMetricOutput,
    private val clock: Clock,
    private val w3cPerformanceTimeline: W3cPerformanceTimeline,
    private val drilldownCondition: Predicate<ActionMetric>
)
{

    @Deprecated(
        message = "Use ActionMeter.Builder instead"
    )
    constructor(
        virtualUser: UUID,
        output: ActionMetricOutput,
        clock: Clock,
        w3cPerformanceTimeline: W3cPerformanceTimeline
    ) : this (
        virtualUser = virtualUser,
        output = output,
        clock = clock,
        w3cPerformanceTimeline = w3cPerformanceTimeline,
        drilldownCondition = Predicate { true }
    )

    @Deprecated(
        message = "Use ActionMeter.builder instead"
    )
    constructor(
        virtualUser: UUID,
        output: ActionMetricOutput = ThrowawayActionMetricOutput(),
        clock: Clock = Clock.systemUTC()
    ) : this(
        virtualUser = virtualUser,
        output = output,
        clock = clock,
        w3cPerformanceTimeline = DisabledW3cPerformanceTimeline(),
        drilldownCondition = Predicate { false }
    )

    /**
     * Measures the latency of the [action].
     *
     * @param action has an interesting latency
     * @param key logically groups the [action]s
     * @param T the type of the result of the [action]
     * @return result of the [action]
     */
    fun <T> measure(
        key: ActionType<*>,
        action: () -> T
    ): T = measure(
        key = key,
        action = action,
        observation = { null }
    )

    /**
     * Measures the latency of the [action].
     * Records the [observation].
     *
     * @param action has an interesting latency
     * @param key logically groups the [action]s
     * @param observation of an interesting fact about the result of the [action]
     * @param T the type of the result of the [action]
     * @return result of the [action]
     */
    fun <T> measure(
        key: ActionType<*>,
        action: () -> T,
        observation: (T) -> JsonObject?
    ): T {
        val start = clock.instant()
        var actionResult: ActionResult = ActionResult.OK
        var result: T? = null

        try {
            result = action()
            return result
        } catch (e: Exception) {
            actionResult = if (e.representsInterrupt()) {
                ActionResult.INTERRUPTED
            } else {
                ActionResult.ERROR
            }
            throw Exception("Action '${key.label}' $actionResult", e)
        } finally {
            val metricBuilder = ActionMetric.Builder(
                label = key.label,
                result = actionResult,
                start = start,
                duration = Duration.between(start, clock.instant())
            )
                .virtualUser(virtualUser)

            result?.let { metricBuilder.observation(observation(result)) }

            var metric = metricBuilder.build()

            if (drilldownCondition.test(metric)) {
                metric = ActionMetric.Builder(metric)
                    .drilldown(w3cPerformanceTimeline.record())
                    .build()
            }
            output.write(metric)
        }
    }

    @Deprecated( message = "Use builder instead")
    fun withW3cPerformanceTimeline(
        w3cPerformanceTimeline: W3cPerformanceTimeline
    ): ActionMeter = ActionMeter(
        virtualUser = virtualUser,
        output = output,
        clock = clock,
        w3cPerformanceTimeline = w3cPerformanceTimeline,
        drilldownCondition = Predicate { true }
    )

    class Builder(
        private val virtualUser: UUID,
        private val output: ActionMetricOutput,
        private val clock: Clock
    ){
        private var w3cPerformanceTimeline: W3cPerformanceTimeline = DisabledW3cPerformanceTimeline()
        private var drilldownCondition: Predicate<ActionMetric> = Predicate { true }

        constructor(meter: ActionMeter): this(
            meter.virtualUser,
            meter.output,
            meter.clock
        ){
            w3cPerformanceTimeline = meter.w3cPerformanceTimeline
            drilldownCondition = meter.drilldownCondition
        }

        fun performanceTimeline(w3cPerformanceTimeline: W3cPerformanceTimeline) = apply { this.w3cPerformanceTimeline = w3cPerformanceTimeline }
        fun drilldownCondition(drilldownCondition: Predicate<ActionMetric>) = apply { this.drilldownCondition = drilldownCondition }

        fun build() : ActionMeter = ActionMeter(
            virtualUser = virtualUser,
            output = output,
            clock = clock,
            w3cPerformanceTimeline = w3cPerformanceTimeline,
            drilldownCondition = drilldownCondition
        )
    }
}

@Deprecated("This is an internal data structure used by ActionMeter. Use ActionMeter public methods instead.")
data class Recording<out T>(
    val result: T,
    val duration: Duration,
    val observation: JsonObject? = null
) {
    internal var drilldown: RecordedPerformanceEntries? = null
}
