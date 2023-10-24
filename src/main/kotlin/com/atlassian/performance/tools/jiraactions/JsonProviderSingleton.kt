package com.atlassian.performance.tools.jiraactions

import javax.json.spi.JsonProvider

internal object JsonProviderSingleton {

    val JSON: JsonProvider = JsonProvider.provider()
}
