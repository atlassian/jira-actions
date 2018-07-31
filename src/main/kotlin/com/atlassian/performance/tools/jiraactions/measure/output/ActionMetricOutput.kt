package com.atlassian.performance.tools.jiraactions.measure.output

import com.atlassian.performance.tools.jiraactions.ActionMetric

interface ActionMetricOutput {

    fun write(metric: ActionMetric)
}