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
    val decodedBodySize: Long
)
