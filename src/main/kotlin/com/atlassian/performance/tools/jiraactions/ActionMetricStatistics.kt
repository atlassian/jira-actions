package com.atlassian.performance.tools.jiraactions

import org.apache.commons.math3.stat.StatUtils
import java.time.Duration


class ActionMetricStatistics(
    private val actionMetrics: List<ActionMetric>
) {

    val errors: Map<String, Int> by lazy {
        actionMetrics
            .asSequence()
            .groupingBy { it.label }
            .fold(0) { totalErrors, metric ->
                totalErrors + if (metric.result == ActionResult.ERROR) 1 else 0
            }
    }

    val sampleSize: Map<String, Int> by lazy {
        actionMetrics
            .asSequence()
            .groupingBy { it.label }
            .fold(0) { totalSampleSize, metric ->
                totalSampleSize + if (metric.result == ActionResult.OK) 1 else 0
            }
    }

    /**
     * Maps actions to the given percentile of their latencies.
     */
    fun percentile(percentile: Int): Map<String, Duration> {
        return actionMetrics
            .asSequence()
            .filter { it.result == ActionResult.OK }
            .groupBy { it.label }
            .mapValues { actionToMetric ->
                actionToMetric
                    .value
                    .asSequence()
                    .map { it.duration.toNanos().toDouble() }
                    .toList()
                    .toDoubleArray()
            }
            .mapValues { actionToNanos -> StatUtils.percentile(actionToNanos.value, percentile.toDouble()) }
            .mapValues { actionToPercentile -> Duration.ofNanos(actionToPercentile.value.toLong()) }
    }
}