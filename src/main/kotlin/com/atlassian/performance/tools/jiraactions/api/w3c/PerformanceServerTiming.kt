package com.atlassian.performance.tools.jiraactions.api.w3c

import java.time.Duration

/**
 * Represents the [PerformanceServerTiming](https://www.w3.org/TR/2023/WD-server-timing-20230411/#the-performanceservertiming-interface).
 */
class PerformanceServerTiming internal constructor(
    val name: String,
    val duration: Duration,
    val description: String
)
