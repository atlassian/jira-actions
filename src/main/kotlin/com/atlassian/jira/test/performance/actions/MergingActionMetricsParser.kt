package com.atlassian.jira.test.performance.actions

import java.io.File
import java.io.InputStream

class MergingActionMetricsParser {

    private val parser = ActionMetricsParser()

    fun parse(
        metrics: List<File>
    ): List<ActionMetric> = metrics
        .filter { it.exists() }
        .map { it.inputStream() }
        .map { parse(it) }
        .flatten()

    private fun parse(stream: InputStream): List<ActionMetric> = stream.use { parser.parse(it) }
}