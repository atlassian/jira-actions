package com.atlassian.performance.tools.jiraactions.measure.output

import com.atlassian.performance.tools.jiraactions.ActionMetric
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class CollectionActionMetricOutput(
    val metrics: MutableCollection<ActionMetric>
) : ActionMetricOutput {

    override fun write(metric: ActionMetric) {
        metrics.add(metric)
    }
}