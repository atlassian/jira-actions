package com.atlassian.performance.tools.jiraactions.api.parser

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.MetricVerboseJsonFormat
import org.apache.logging.log4j.LogManager
import java.io.InputStream
import java.io.StringReader
import javax.json.Json
import javax.json.JsonStructure

class ActionMetricsParser {

    private val logger = LogManager.getLogger(this::class.java)
    private val format = MetricVerboseJsonFormat()

    fun parse(
        metricsStream: InputStream
    ): List<ActionMetric> = metricsStream
        .bufferedReader()
        .lineSequence()
        .mapNotNull { parseOrNull(it) }
        .map { format.deserialize(it.asJsonObject()) }
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
