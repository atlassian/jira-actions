package com.atlassian.performance.tools.jiraactions.api.parser

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.format.MetricJsonFormat
import com.atlassian.performance.tools.jiraactions.api.format.MetricVerboseJsonFormat
import java.io.File
import java.util.stream.Stream
import kotlin.streams.toList

class MergingActionMetricsParser(format: MetricJsonFormat) {

    private val parser = ActionMetricsParser(format)

    constructor(): this(MetricVerboseJsonFormat())

    @Deprecated("Accumulating results in lists leads to memory leaks", ReplaceWith("stream(metrics)"))
    fun parse(
        metrics: List<File>
    ): List<ActionMetric> = stream(metrics).toList()

    fun stream(
        metrics: List<File>
    ): Stream<ActionMetric> = metrics
        .stream()
        .filter { it.exists() }
        .map { stream(it) }
        .flatMap { it }

    private fun stream(
        file: File
    ): Stream<ActionMetric> {
        val input = file.inputStream()
        return parser
            .stream(input)
            .onClose { input.close() }
    }
}
