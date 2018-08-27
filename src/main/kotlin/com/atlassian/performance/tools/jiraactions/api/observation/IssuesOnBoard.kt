package com.atlassian.performance.tools.jiraactions.api.observation

import javax.json.Json
import javax.json.JsonObject

data class IssuesOnBoard(val issues: Int) {
    constructor(json: JsonObject) : this(json.getInt("issues"))

    fun serialize(): JsonObject = Json.createObjectBuilder().add("issues", issues).build()
}