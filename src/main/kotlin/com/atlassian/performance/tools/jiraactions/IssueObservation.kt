package com.atlassian.performance.tools.jiraactions

import javax.json.Json
import javax.json.JsonObject

data class IssueObservation(
    val issueKey: String
) {
    constructor(json: JsonObject) : this(json.getString("issueKey"))

    fun serialize(): JsonObject = Json.createObjectBuilder()
        .add("issueKey", issueKey)
        .build()
}