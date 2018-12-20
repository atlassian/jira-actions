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
import javax.json.JsonObject

/**
 * Records multiple measurements and observations of various actions.
 *
 * @param virtualUser identifies a virtual user
 * @param output writes the metrics
 * @param clock measures latencies
 * @param w3cPerformanceTimeline records W3C performance entries
 */
class ActionMeter(
    private val virtualUser: UUID,
    private val output: ActionMetricOutput,
    private val clock: Clock,
    private val w3cPerformanceTimeline: W3cPerformanceTimeline
) {

    @Deprecated(
        message = "Use the primary constructor"
    )
    constructor(
        virtualUser: UUID,
        output: ActionMetricOutput = ThrowawayActionMetricOutput(),
        clock: Clock = Clock.systemUTC()
    ) : this(
        virtualUser = virtualUser,
        output = output,
        clock = clock,
        w3cPerformanceTimeline = DisabledW3cPerformanceTimeline()
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
        return record(key) {
            val result = action()
            val duration = Duration.between(start, clock.instant())
            val recording = Recording(result, duration, observation(result))
            recording.drilldown = w3cPerformanceTimeline.record()
            return@record recording
        }
    }

    /**
     * Records the latency and an optional observation of the self-measuring [action].
     *
     * @param action can measure and observe itself
     * @param key logically groups the [action]s
     * @param T the type of the result of the [action]
     * @return result of the [action]
     */
    private fun <T> record(
        key: ActionType<*>,
        action: () -> Recording<T>
    ): T {
        val start = clock.instant()
        try {
            val recording = action()
            output.write(
                ActionMetric(
                    label = key.label,
                    result = ActionResult.OK,
                    start = start,
                    duration = recording.duration,
                    virtualUser = virtualUser,
                    observation = recording.observation,
                    drilldown = recording.drilldown
                )
            )
            return recording.result
        } catch (e: Exception) {
            val result = if (e.representsInterrupt()) {
                ActionResult.INTERRUPTED
            } else {
                ActionResult.ERROR
            }
            output.write(
                ActionMetric(
                    label = key.label,
                    result = result,
                    start = start,
                    duration = Duration.between(start, clock.instant()),
                    virtualUser = virtualUser,
                    observation = null,
                    drilldown = null
                )
            )
            throw Exception("Action '${key.label}' $result", e)
        }
    }

    fun withW3cPerformanceTimeline(
        w3cPerformanceTimeline: W3cPerformanceTimeline
    ): ActionMeter = ActionMeter(
        virtualUser = virtualUser,
        output = output,
        clock = clock,
        w3cPerformanceTimeline = w3cPerformanceTimeline
    )
}

data class Recording<out T>(
    val result: T,
    val duration: Duration,
    val observation: JsonObject? = null
) {
    internal var drilldown: RecordedPerformanceEntries? = null
}
