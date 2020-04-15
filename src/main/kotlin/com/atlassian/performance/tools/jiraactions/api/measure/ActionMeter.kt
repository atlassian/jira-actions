package com.atlassian.performance.tools.jiraactions.api.measure

import com.atlassian.performance.tools.concurrency.api.representsInterrupt
import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.measure.output.ActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.measure.output.ThrowawayActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.w3c.RecordedPerformanceEntries
import com.atlassian.performance.tools.jiraactions.api.w3c.W3cPerformanceTimeline
import java.time.Clock
import java.time.Duration
import java.util.*
import javax.json.JsonObject

/**
 * Records multiple measurements and observations of various actions.
 *
 * @param virtualUser identifies a virtual user
 * @param output writes the metrics
 * @param clock measures latencies
 */
class ActionMeter private constructor(
    private val virtualUser: UUID,
    private val output: ActionMetricOutput,
    private val clock: Clock,
    private val postMetricHooks: List<PostMetricHook>
) {

    @Deprecated(
        message = "Use ActionMeter.Builder instead",
        replaceWith = ReplaceWith("ActionMeter.Builder(" +
            "\noutput = output" +
            "\n)" +
            "\n.clock(clock)" +
            "\n.virtualUser(virtualUser)" +
            "\n.build()",
            "com.atlassian.performance.tools.jiraactions.api.measure.DrillDownHook"
        )
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
        postMetricHooks = listOf(DrillDownHook(w3cPerformanceTimeline))
    )

    @Deprecated(
        message = "Use ActionMeter.Builder instead",
        replaceWith = ReplaceWith("ActionMeter.Builder(" +
            "\noutput = output" +
            "\n)" +
            "\n.clock(clock)" +
            "\n.virtualUser(virtualUser)" +
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
        postMetricHooks = emptyList()
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
            postMetricHooks.forEach { it.run(actionMetricBuilder) }
            result?.let { actionMetricBuilder.observation(observation(result)) }
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
            postMetricHooks.forEach { it.run(actionMetricBuilder) }
            output.write(actionMetricBuilder.build())
            throw Exception("Action '${key.label}' $actionResult", e)
        }
    }

    @Deprecated(message = "Use ActionMeter.Builder instead")
    fun withW3cPerformanceTimeline(
        w3cPerformanceTimeline: W3cPerformanceTimeline
    ): ActionMeter = ActionMeter(
        virtualUser = virtualUser,
        output = output,
        clock = clock,
        postMetricHooks = emptyList()
    )

    class Builder(
        private var output: ActionMetricOutput
    ) {
        private var virtualUser: UUID = UUID.randomUUID()
        private var clock = Clock.systemUTC()
        private var postMetricHooks: MutableList<PostMetricHook> = mutableListOf()

        constructor(meter: ActionMeter) : this(
            meter.output
        ) {
            clock = meter.clock
            virtualUser = meter.virtualUser
        }

        fun clock(clock: Clock) = apply { this.clock = clock }
        fun appendPostMetricHook(postMetricHook: PostMetricHook) = apply { this.postMetricHooks.add(postMetricHook) }
        fun virtualUser(virtualUser: UUID) = apply { this.virtualUser = virtualUser }
        fun overrideOutput(outputProvider: (ActionMetricOutput) -> ActionMetricOutput) = apply { this.output = outputProvider.invoke(output) }

        fun build(): ActionMeter = ActionMeter(
            virtualUser = virtualUser,
            output = output,
            clock = clock,
            postMetricHooks = postMetricHooks
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
