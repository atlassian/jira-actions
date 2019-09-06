package com.atlassian.performance.tools.jiraactions.api.format

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.w3c.VerboseJsonFormat
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.json.Json
import javax.json.JsonObject

class MetricVerboseJsonFormat : MetricJsonFormat {

    private val drilldownFormat = VerboseJsonFormat()

    override fun serialize(
        actionMetric: ActionMetric
    ): JsonObject = actionMetric.run {
        Json.createObjectBuilder()
            .add("label", label)
            .add("result", result.name)
            .add("duration", duration.toString())
            .add("start", start.toString())
            .add("virtualUser", virtualUser.toString())
            .also { json -> observation?.let { json.add("observation", it) } }
            .also { json -> drilldown?.let { json.add("drilldown", drilldownFormat.serializeRecordedEntries(it)) } }
            .build()
    }

    override fun deserialize(
        json: JsonObject
    ): ActionMetric = json.run {
        ActionMetric.Builder(
            label = getString("label"),
            result = getString("result").let { ActionResult.valueOf(it) },
            duration = getString("duration").let { Duration.parse(it) },
            start = getString("start").let { Instant.parse(it) }
        )
            .virtualUser(getString("virtualUser").let { UUID.fromString(it) })
            .observation(getJsonObject("observation"))
            .drilldown(getJsonObject("drilldown")?.let { drilldownFormat.deserializeRecordedEntries(it) })
            .build()
    }
}
