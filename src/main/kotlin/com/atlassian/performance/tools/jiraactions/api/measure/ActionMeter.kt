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
    private val drilldownCondition: Predicate<ActionMetric>,
    private val postMetricHook: PostMetricHook
) {

    @Deprecated(
        message = "Use ActionMeter.Builder instead",
        replaceWith = ReplaceWith("ActionMeter.Builder(" +
            "\nvirtualUser = virtualUser," +
            "\noutput = output" +
            "\n)" +
            "\n.clock(clock)" +
            "\n.performanceTimeline(w3cPerformanceTimeline)" +
            "\n.build()")
    )
    constructor(
        virtualUser: UUID,
        output: ActionMetricOutput,
        clock: Clock,
        w3cPerformanceTimeline: W3cPerformanceTimeline
    ) : this(
        virtualUser = virtualUser,
        output = output,
        clock = clock,
        w3cPerformanceTimeline = w3cPerformanceTimeline,
        drilldownCondition = Predicate { true },
        postMetricHook = NoopPostMetricHook()
    )

    @Deprecated(
        message = "Use ActionMeter.Builder instead",
        replaceWith = ReplaceWith("ActionMeter.Builder(" +
            "\nvirtualUser = virtualUser," +
            "\noutput = output" +
            "\n)" +
            "\n.clock(clock)" +
            "\n.build()")
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
        drilldownCondition = Predicate { true },
        postMetricHook = NoopPostMetricHook()
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
        try {
            val result = action()
            val duration = Duration.between(start, clock.instant())
            val actionMetricBuilder = ActionMetric.Builder(
                label = key.label,
                start = start,
                duration = duration,
                result = ActionResult.OK
            ).virtualUser(virtualUser)
            postMetricHook.run(actionMetricBuilder)
            result?.let { actionMetricBuilder.observation(observation(result)) }
            conditionallyAddDrilldown(actionMetricBuilder)
            output.write(actionMetricBuilder.build())
            return result
        } catch (e: Exception) {
            val duration = Duration.between(start, clock.instant())
            val actionResult = if (e.representsInterrupt()) {
                ActionResult.INTERRUPTED
            } else {
                ActionResult.ERROR
            }
            val actionMetricBuilder = ActionMetric.Builder(
                label = key.label,
                start = start,
                duration = duration,
                result = actionResult
            ).virtualUser(virtualUser)
            postMetricHook.run(actionMetricBuilder)
            conditionallyAddDrilldown(actionMetricBuilder)
            output.write(actionMetricBuilder.build())
            throw Exception("Action '${key.label}' $actionResult", e)
        }
    }

    private fun conditionallyAddDrilldown(actionMetricBuilder: ActionMetric.Builder): ActionMetric {
        return actionMetricBuilder.build().let {
            if (drilldownCondition.test(it)) {
                actionMetricBuilder
                    .drilldown(w3cPerformanceTimeline.record())
                    .build()
            } else {
                it
            }
        }
    }

    @Deprecated(message = "Use ActionMeter.Builder instead")
    fun withW3cPerformanceTimeline(
        w3cPerformanceTimeline: W3cPerformanceTimeline
    ): ActionMeter = ActionMeter(
        virtualUser = virtualUser,
        output = output,
        clock = clock,
        w3cPerformanceTimeline = w3cPerformanceTimeline,
        drilldownCondition = Predicate { true },
        postMetricHook = NoopPostMetricHook()
    )

    class Builder(
        private val output: ActionMetricOutput
    ) {
        private var virtualUser: UUID = UUID.randomUUID()
        private var w3cPerformanceTimeline: W3cPerformanceTimeline = DisabledW3cPerformanceTimeline()
        private var drilldownCondition: Predicate<ActionMetric> = Predicate { true }
        private var clock = Clock.systemUTC()
        private var postMetricHook: PostMetricHook = NoopPostMetricHook()

        constructor(meter: ActionMeter) : this(
            meter.output
        ) {
            clock = meter.clock
            w3cPerformanceTimeline = meter.w3cPerformanceTimeline
            drilldownCondition = meter.drilldownCondition
            virtualUser = meter.virtualUser
        }

        fun performanceTimeline(w3cPerformanceTimeline: W3cPerformanceTimeline) = apply { this.w3cPerformanceTimeline = w3cPerformanceTimeline }
        fun drilldownCondition(drilldownCondition: Predicate<ActionMetric>) = apply { this.drilldownCondition = drilldownCondition }
        fun clock(clock: Clock) = apply { this.clock = clock }
        fun postMetricHook(postMetricHook: PostMetricHook) = apply { this.postMetricHook = postMetricHook }

        fun virtualUser(virtualUser: UUID) = apply { this.virtualUser = virtualUser }

        fun build(): ActionMeter = ActionMeter(
            virtualUser = virtualUser,
            output = output,
            clock = clock,
            w3cPerformanceTimeline = w3cPerformanceTimeline,
            drilldownCondition = drilldownCondition,
            postMetricHook = postMetricHook
        )
    }

    private class NoopPostMetricHook : PostMetricHook {
        override fun run(actionMetricBuilder: ActionMetric.Builder) {
        }
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
