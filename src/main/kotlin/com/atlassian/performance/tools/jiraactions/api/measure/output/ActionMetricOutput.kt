package com.atlassian.performance.tools.jiraactions.api.measure.output

import com.atlassian.performance.tools.jiraactions.api.ActionMetric

interface ActionMetricOutput {

    fun write(metric: ActionMetric)
}