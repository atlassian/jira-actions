package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.api.w3c.RecordedPerformanceEntries
import com.atlassian.performance.tools.jiraactions.w3c.VerboseJsonFormat
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.json.Json
import javax.json.JsonObject

/**
 * @deprecated The generated `copy` and `componentN` methods should not be used. It will become a non-data class.
 */
data class ActionMetric @Deprecated("Use ActionMetric.Builder instead.") constructor(
    val label: String,
    val result: ActionResult,
    val duration: Duration,
    val start: Instant,
    val virtualUser: UUID,
    val observation: JsonObject? = null
) {
    /**
     * ActionMetric was immutable before this. We do this to leave the primary constructor intact to avoid breaking
     * the data-class-copy.
     * We can make it immutable again after removal of the deprecated constructor.
     */
    var drilldown: RecordedPerformanceEntries? = null
        private set

    val end: Instant = start + duration

    @Deprecated("Use ActionMetricsParser instead.")
    constructor(serialized: JsonObject) : this(
        serialized.getString("label"),
        serialized.getString("result").let { ActionResult.valueOf(it) },
        serialized.getString("duration").let { Duration.parse(it) },
        serialized.getString("start").let { Instant.parse(it) },
        serialized.getString("virtualUser").let { UUID.fromString(it) },
        serialized.getJsonObject("observation"),
        serialized.getJsonObject("drilldown")?.let { VerboseJsonFormat().deserializeRecordedEntries(it) }
    )

    @Suppress("DEPRECATION")
    @Deprecated("Use ActionMetric.Builder instead.")
    internal constructor(
        label: String,
        result: ActionResult,
        duration: Duration,
        start: Instant,
        virtualUser: UUID,
        observation: JsonObject?,
        drilldown: RecordedPerformanceEntries?
    ) : this(
        label = label,
        result = result,
        duration = duration,
        start = start,
        virtualUser = virtualUser,
        observation = observation
    ) {
        this.drilldown = drilldown
    }

    @Deprecated("Use AppendableActionMetricOutput instead.")
    fun toJson(): JsonObject {
        val builder = Json.createObjectBuilder()
            .add("label", label)
            .add("result", result.name)
            .add("duration", duration.toString())
            .add("start", start.toString())
            .add("virtualUser", virtualUser.toString())

        observation?.let { builder.add("observation", it) }
        drilldown?.let { builder.add("drilldown", VerboseJsonFormat().serializeRecordedEntries(it)) }
        return builder.build()
    }

    class Builder(
        private val label: String,
        private val result: ActionResult,
        private val duration: Duration,
        private val start: Instant
    ) {
        private var observation: JsonObject? = null
        private var drilldown: RecordedPerformanceEntries? = null
        private var virtualUser: UUID = UUID.randomUUID()


        constructor(actionMetric: ActionMetric) : this(
            actionMetric.label,
            actionMetric.result,
            actionMetric.duration,
            actionMetric.start
        ){
            observation = actionMetric.observation
            drilldown = actionMetric.drilldown
            virtualUser = actionMetric.virtualUser
        }

        fun observation(observation: JsonObject?) = apply { this.observation = observation }
        fun drilldown(drilldown: RecordedPerformanceEntries?) = apply { this.drilldown = drilldown }
        fun virtualUser(virtualUser: UUID) = apply { this.virtualUser = virtualUser }

        @Suppress("DEPRECATION")
        fun build() = ActionMetric(
            label = label,
            result = result,
            duration = duration,
            start = start,
            virtualUser = virtualUser,
            observation = observation,
            drilldown = drilldown
        )
    }
}

enum class ActionResult {
    OK,
    ERROR,
    INTERRUPTED
}
