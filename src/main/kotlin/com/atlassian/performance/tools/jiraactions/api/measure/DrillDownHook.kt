package com.atlassian.performance.tools.jiraactions.api.measure

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.w3c.W3cPerformanceTimeline

class DrillDownHook(
    private val w3cPerformanceTimeline: W3cPerformanceTimeline
) : PostMetricHook {
    override fun run(actionMetricBuilder: ActionMetric.Builder) {
        actionMetricBuilder.drilldown(w3cPerformanceTimeline.record())
    }
}

