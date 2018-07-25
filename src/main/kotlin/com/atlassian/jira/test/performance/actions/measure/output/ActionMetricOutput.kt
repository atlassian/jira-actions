package com.atlassian.jira.test.performance.actions.measure.output

import com.atlassian.jira.test.performance.actions.ActionMetric

interface ActionMetricOutput {

    fun write(metric: ActionMetric)
}