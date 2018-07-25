package com.atlassian.jira.test.performance.actions.measure.output

import com.atlassian.jira.test.performance.actions.ActionMetric
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class AppendableActionMetricOutput(
    private val target: Appendable
) : ActionMetricOutput {

    override fun write(metric: ActionMetric) {
        target.append(metric.toJson().toString())
        target.append('\n')
    }
}