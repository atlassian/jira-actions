package com.atlassian.jira.test.performance.actions.measure.output

import com.atlassian.jira.test.performance.actions.ActionMetric
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class CollectionActionMetricOutput(
    val metrics: MutableCollection<ActionMetric>
) : ActionMetricOutput {

    override fun write(metric: ActionMetric) {
        metrics.add(metric)
    }
}