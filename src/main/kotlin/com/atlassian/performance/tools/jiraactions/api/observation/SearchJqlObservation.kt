package com.atlassian.performance.tools.jiraactions.api.observation

import com.atlassian.performance.tools.jiraactions.JsonProviderSingleton.JSON
import javax.json.JsonObject

data class SearchJqlObservation(
    val jql: String,
    val issues: Int,
    val totalResults: Int
) {
    constructor(json: JsonObject) : this(json.getString("jql"), json.getInt("issues"), json.getInt("totalResults"))

    fun serialize(): JsonObject = JSON.createObjectBuilder()
        .add("jql", jql)
        .add("issues", issues)
        .add("totalResults", totalResults)
        .build()
}
