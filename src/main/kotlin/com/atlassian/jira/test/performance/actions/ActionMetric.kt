package com.atlassian.jira.test.performance.actions

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
    val end: Instant = start + duration

    constructor(serialized: JsonObject) : this(
        serialized.getString("label"),
        serialized.getString("result").let { ActionResult.valueOf(it) },
        serialized.getString("duration").let { Duration.parse(it) },
        serialized.getString("start").let { Instant.parse(it) },
        serialized.getString("virtualUser").let { UUID.fromString(it) },
        serialized.getJsonObject("observation")
    )

    fun toJson(): JsonObject {
        val builder = Json.createObjectBuilder()
            .add("label", label)
            .add("result", result.name)
            .add("duration", duration.toString())
            .add("start", start.toString())
            .add("virtualUser", virtualUser.toString())

        observation?.let {  builder.add("observation", it) }
        return builder.build()
    }
}

enum class ActionResult {
    OK,
    ERROR,
    INTERRUPTED
}