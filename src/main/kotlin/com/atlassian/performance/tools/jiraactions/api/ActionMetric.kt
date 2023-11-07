package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.api.w3c.RecordedPerformanceEntries
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.json.JsonObject

data class ActionMetric internal constructor(
    val label: String,
    val result: ActionResult,
    val duration: Duration,
    val start: Instant,
    val virtualUser: UUID,
    val observation: JsonObject? = null,
    var drilldown: RecordedPerformanceEntries? = null
) {
    val end: Instant = start + duration

    class Builder(
        private val label: String,
        private val result: ActionResult,
        private val duration: Duration,
        private val start: Instant
    ) {
        private var observation: JsonObject? = null
        private var drilldown: RecordedPerformanceEntries? = null
        private var virtualUser: UUID = UUID.randomUUID()

        fun observation(observation: JsonObject?) = apply { this.observation = observation }
        fun drilldown(drilldown: RecordedPerformanceEntries?) = apply { this.drilldown = drilldown }
        fun virtualUser(virtualUser: UUID) = apply { this.virtualUser = virtualUser }

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
