package com.atlassian.performance.tools.jiraactions.api.w3c

/**
 * Represents the [W3C performance timeline](https://www.w3.org/TR/performance-timeline-2/).
 */
interface W3cPerformanceTimeline {
    fun record(): RecordedPerformanceEntries?
}
