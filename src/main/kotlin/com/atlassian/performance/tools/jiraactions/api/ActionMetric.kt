package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.api.w3c.RecordedPerformanceEntries
import com.atlassian.performance.tools.jiraactions.w3c.VerboseJsonFormat
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.json.Json
import javax.json.JsonObject

data class ActionMetric(
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

    constructor(serialized: JsonObject) : this(
        serialized.getString("label"),
        serialized.getString("result").let { ActionResult.valueOf(it) },
        serialized.getString("duration").let { Duration.parse(it) },
        serialized.getString("start").let { Instant.parse(it) },
        serialized.getString("virtualUser").let { UUID.fromString(it) },
        serialized.getJsonObject("observation"),
        serialized.getJsonObject("drilldown")?.let { VerboseJsonFormat().deserializeRecordedEntries(it) }
    )

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
}

enum class ActionResult {
    OK,
    ERROR,
    INTERRUPTED
}