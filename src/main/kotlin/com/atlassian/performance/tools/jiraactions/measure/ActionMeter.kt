package com.atlassian.performance.tools.jiraactions.measure

import com.atlassian.performance.tools.concurrency.representsInterrupt
import com.atlassian.performance.tools.jiraactions.ActionType
import com.atlassian.performance.tools.jiraactions.measure.output.ActionMetricOutput
import com.atlassian.performance.tools.jiraactions.measure.output.ThrowawayActionMetricOutput
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
class ActionMeter(
    private val virtualUser: UUID,
    private val output: ActionMetricOutput = ThrowawayActionMetricOutput(),
    private val clock: Clock = Clock.systemUTC()
) {

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
            Recording(result, duration, observation(result))
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
                com.atlassian.performance.tools.jiraactions.ActionMetric(
                    label = key.label,
                    result = com.atlassian.performance.tools.jiraactions.ActionResult.OK,
                    start = start,
                    duration = recording.duration,
                    virtualUser = virtualUser,
                    observation = recording.observation
                )
            )
            return recording.result
        } catch (e: Exception) {
            val result = if (e.representsInterrupt()) {
                com.atlassian.performance.tools.jiraactions.ActionResult.INTERRUPTED
            } else {
                com.atlassian.performance.tools.jiraactions.ActionResult.ERROR
            }
            output.write(
                com.atlassian.performance.tools.jiraactions.ActionMetric(
                    label = key.label,
                    result = result,
                    start = start,
                    duration = Duration.between(start, clock.instant()),
                    virtualUser = virtualUser
                )
            )
            throw Exception("Action '${key.label}' $result", e)
        }
    }

}

data class Recording<out T>(
    val result: T,
    val duration: Duration,
    val observation: JsonObject? = null
)