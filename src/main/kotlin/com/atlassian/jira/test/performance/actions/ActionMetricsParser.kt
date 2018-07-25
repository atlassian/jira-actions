package com.atlassian.jira.test.performance.actions

import org.apache.logging.log4j.LogManager
import java.io.InputStream
import java.io.StringReader
import javax.json.Json
import javax.json.JsonStructure

class ActionMetricsParser {

    private val logger = LogManager.getLogger(this::class.java)

    fun parse(
        metricsStream: InputStream
    ): List<ActionMetric> = metricsStream
        .bufferedReader()
        .lineSequence()
        .mapNotNull { parseOrNull(it) }
        .map { ActionMetric(it.asJsonObject()) }
        .toList()

    private fun parseOrNull(
        line: String
    ): JsonStructure? = try {
        Json.createReader(StringReader(line)).read()
    } catch (e: Exception) {
        logger.debug("Discarding '$line'", e)
        null
    }
}
