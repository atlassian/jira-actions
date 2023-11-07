package com.atlassian.performance.tools.jiraactions.api.parser

import com.atlassian.performance.tools.jiraactions.JsonProviderSingleton.JSON
import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.format.MetricJsonFormat
import com.atlassian.performance.tools.jiraactions.api.format.MetricVerboseJsonFormat
import org.apache.logging.log4j.LogManager
import java.io.InputStream
import java.io.StringReader
import java.util.stream.Stream
import javax.json.JsonStructure
import kotlin.streams.asStream

class ActionMetricsParser(private val format: MetricJsonFormat) {

    private val logger = LogManager.getLogger(this::class.java)

    constructor(): this(MetricVerboseJsonFormat())

    fun stream(
        metricsStream: InputStream
    ): Stream<ActionMetric> = metricsStream
        .bufferedReader()
        .lineSequence()
        .mapNotNull { parseOrNull(it) }
        .map { format.deserialize(it.asJsonObject()) }
        .asStream()

    private fun parseOrNull(
        line: String
    ): JsonStructure? = try {
        JSON.createReader(StringReader(line)).read()
    } catch (e: Exception) {
        logger.debug("Discarding '$line'", e)
        null
    }
}
