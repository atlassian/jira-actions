package com.atlassian.performance.tools.jiraactions.api.w3c

import java.time.Duration

/**
 * Represents the [PerformanceResourceTiming](https://www.w3.org/TR/resource-timing-2/#dom-performanceresourcetiming).
 */
class PerformanceResourceTiming internal constructor(
    val entry: PerformanceEntry,
    val initiatorType: String,
    val nextHopProtocol: String,
    val workerStart: Duration,
    val redirectStart: Duration,
    val redirectEnd: Duration,
    val fetchStart: Duration,
    val domainLookupStart: Duration,
    val domainLookupEnd: Duration,
    val connectStart: Duration,
    val connectEnd: Duration,
    val secureConnectionStart: Duration,
    val requestStart: Duration,
    val responseStart: Duration,
    val responseEnd: Duration,
    val transferSize: Long,
    val encodedBodySize: Long,
    val decodedBodySize: Long,
    /**
     * Represents the [serverTiming attribute](https://www.w3.org/TR/2023/WD-server-timing-20230411/#servertiming-attribute)
     *
     * @return Null if blocked, e.g. by same-origin policy.
     *         Empty if not blocked, but server didn't send any `Server-Timing` headers.
     * @since 3.24.0
     */
    val serverTiming: List<PerformanceServerTiming>?
)
