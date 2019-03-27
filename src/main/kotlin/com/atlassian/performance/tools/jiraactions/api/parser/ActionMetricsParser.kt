package com.atlassian.performance.tools.jiraactions.api.parser

import com.atlassian.performance.tools.jiraactions.MetricVerboseJsonFormat
import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import org.apache.logging.log4j.LogManager
import java.io.InputStream
import java.io.StringReader
import java.util.stream.Stream
import javax.json.Json
import javax.json.JsonStructure
import kotlin.streams.asStream
import kotlin.streams.toList

class ActionMetricsParser {

    private val logger = LogManager.getLogger(this::class.java)
    private val format = MetricVerboseJsonFormat()

    @Deprecated("Accumulating results in lists leads to memory leaks", ReplaceWith("stream(metricsStream)"))
    fun parse(
        metricsStream: InputStream
    ): List<ActionMetric> = stream(metricsStream).toList()

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
        Json.createReader(StringReader(line)).read()
    } catch (e: Exception) {
        logger.debug("Discarding '$line'", e)
        null
    }
}
