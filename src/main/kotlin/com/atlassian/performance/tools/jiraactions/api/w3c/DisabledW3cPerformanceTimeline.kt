package com.atlassian.performance.tools.jiraactions.api.w3c

/**
 * Yields no entries.
 * Has no overhead on CPU, memory or I/O.
 */
class DisabledW3cPerformanceTimeline : W3cPerformanceTimeline {
    override fun record(): RecordedPerformanceEntries? = null
}