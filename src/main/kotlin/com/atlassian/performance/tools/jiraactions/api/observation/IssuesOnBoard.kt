package com.atlassian.performance.tools.jiraactions.api.observation

import com.atlassian.performance.tools.jiraactions.JsonProviderSingleton.JSON
import javax.json.JsonObject

data class IssuesOnBoard(val issues: Int) {
    constructor(json: JsonObject) : this(json.getInt("issues"))

    fun serialize(): JsonObject = JSON.createObjectBuilder().add("issues", issues).build()
}
