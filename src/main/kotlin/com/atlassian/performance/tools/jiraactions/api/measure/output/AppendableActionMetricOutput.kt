package com.atlassian.performance.tools.jiraactions.api.measure.output

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
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