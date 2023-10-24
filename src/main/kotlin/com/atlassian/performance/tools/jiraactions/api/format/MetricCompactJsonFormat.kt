package com.atlassian.performance.tools.jiraactions.api.format

import com.atlassian.performance.tools.jiraactions.JsonProviderSingleton.JSON
import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.json.JsonObject

/**
 * Format ignoring drilldown parameter
 */
class MetricCompactJsonFormat: MetricJsonFormat {

    override fun serialize(
        actionMetric: ActionMetric
    ): JsonObject = actionMetric.run {
        JSON.createObjectBuilder()
            .add("label", label)
            .add("result", result.name)
            .add("duration", duration.toString())
            .add("start", start.toString())
            .add("virtualUser", virtualUser.toString())
            .also { json -> observation?.let { json.add("observation", it) } }
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
            .build()
    }

}
