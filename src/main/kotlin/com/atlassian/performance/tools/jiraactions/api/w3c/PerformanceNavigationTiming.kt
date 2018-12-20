package com.atlassian.performance.tools.jiraactions.api.w3c

import java.time.Duration

/**
 * Represents the [PerformanceNavigationTiming](https://www.w3.org/TR/navigation-timing-2/#dom-performancenavigationtiming).
 */
class PerformanceNavigationTiming internal constructor(
    val resource: PerformanceResourceTiming,
    val unloadEventStart: Duration,
    val unloadEventEnd: Duration,
    val domInteractive: Duration,
    val domContentLoadedEventStart: Duration,
    val domContentLoadedEventEnd: Duration,
    val domComplete: Duration,
    val loadEventStart: Duration,
    val loadEventEnd: Duration,
    val type: NavigationType,
    val redirectCount: Int
)
