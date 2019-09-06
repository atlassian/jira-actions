package com.atlassian.performance.tools.jiraactions.api.format

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import javax.json.JsonObject

interface MetricJsonFormat {
    fun serialize(actionMetric: ActionMetric): JsonObject

    fun deserialize(json: JsonObject): ActionMetric
}
