package com.atlassian.performance.tools.jiraactions.api.observation

import com.atlassian.performance.tools.jiraactions.JsonProviderSingleton.JSON
import javax.json.JsonObject

data class IssueObservation(
    val issueKey: String
) {
    constructor(json: JsonObject) : this(json.getString("issueKey"))

    fun serialize(): JsonObject = JSON.createObjectBuilder()
        .add("issueKey", issueKey)
        .build()
}
