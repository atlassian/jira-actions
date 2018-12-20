package com.atlassian.performance.tools.jiraactions.api.w3c

import java.time.Duration

/**
 * Represents the [PerformanceEntry](https://www.w3.org/TR/performance-timeline-2/#the-performanceentry-interface).
 */
class PerformanceEntry internal constructor(
    val name: String,
    val entryType: String,
    val startTime: Duration,
    val duration: Duration
)
