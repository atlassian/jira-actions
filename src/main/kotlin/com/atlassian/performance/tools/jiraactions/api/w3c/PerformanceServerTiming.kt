package com.atlassian.performance.tools.jiraactions.api.w3c

import java.time.Duration

/**
 * Represents the [PerformanceServerTiming](https://www.w3.org/TR/2023/WD-server-timing-20230411/#the-performanceservertiming-interface).
 *
 * @since 3.24.0
 */
class PerformanceServerTiming internal constructor(
    val name: String,
    val duration: Duration,
    val description: String
)
