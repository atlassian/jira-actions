package com.atlassian.performance.tools.jiraactions.api.jql

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage

interface JqlContext {
    fun seededRandom(): SeededRandom
    fun issuePage(): IssuePage?
}

interface JqlSupplier {
    fun get(context: JqlContext): Jql?
    fun uniqueName(): String
}

interface Jql {
    fun query(): String
    fun supplier(): JqlSupplier
}
