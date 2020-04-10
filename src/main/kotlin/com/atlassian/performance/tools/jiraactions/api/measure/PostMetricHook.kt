package com.atlassian.performance.tools.jiraactions.api.measure

import com.atlassian.performance.tools.jiraactions.api.ActionMetric

interface PostMetricHook {
    fun run(actionMetricBuilder : ActionMetric.Builder)
}
