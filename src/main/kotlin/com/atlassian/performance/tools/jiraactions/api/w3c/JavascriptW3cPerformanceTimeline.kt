package com.atlassian.performance.tools.jiraactions.api.w3c

import org.openqa.selenium.JavascriptExecutor
import java.time.Duration

/**
 * Obtains entries from [javascript].
 */
class JavascriptW3cPerformanceTimeline(
    private val javascript: JavascriptExecutor
) : W3cPerformanceTimeline {

    override fun record(): RecordedPerformanceEntries? {
        val jsResources = javascript.executeScript("return window.performance.getEntriesByType(\"resource\");")
        val jsNavigations = javascript.executeScript("return window.performance.getEntriesByType(\"navigation\");")
        return RecordedPerformanceEntries(
            navigations = parseNavigations(jsNavigations),
            resources = parseResources(jsResources)
        )
    }

    private fun parseNavigations(
        jsNavigations: Any?
    ): List<PerformanceNavigationTiming> {
        if (jsNavigations !is List<*>) {
            throw Exception("Unexpected non-list JavaScript value: $jsNavigations")
        }
        return jsNavigations.map { parsePerformanceNavigationTiming(it) }
    }

    private fun parsePerformanceNavigationTiming(
        map: Any?
    ): PerformanceNavigationTiming {
        if (map !is Map<*, *>) {
            throw Exception("Unexpected non-map JavaScript value: $map")
        }
        return PerformanceNavigationTiming(
            resource = parsePerformanceResourceTiming(map),
            unloadEventStart = parseTimestamp(map["unloadEventStart"]),
            unloadEventEnd = parseTimestamp(map["unloadEventEnd"]),
            domInteractive = parseTimestamp(map["domInteractive"]),
            domContentLoadedEventStart = parseTimestamp(map["domContentLoadedEventStart"]),
            domContentLoadedEventEnd = parseTimestamp(map["domContentLoadedEventEnd"]),
            domComplete = parseTimestamp(map["domComplete"]),
            loadEventStart = parseTimestamp(map["loadEventStart"]),
            loadEventEnd = parseTimestamp(map["loadEventEnd"]),
            type = parseNavigationType(map["type"]),
            redirectCount = (map["redirectCount"] as Long).toInt()
        )
    }

    private fun parseResources(
        jsResources: Any
    ): List<PerformanceResourceTiming> {
        if (jsResources !is List<*>) {
            throw Exception("Unexpected non-list JavaScript value: $jsResources")
        }
        return jsResources.map { parsePerformanceResourceTiming(it) }
    }

    private fun parsePerformanceResourceTiming(
        map: Any?
    ): PerformanceResourceTiming {
        if (map !is Map<*, *>) {
            throw Exception("Unexpected non-map JavaScript value: $map")
        }
        return PerformanceResourceTiming(
            entry = parsePerformanceEntry(map),
            initiatorType = map["initiatorType"] as String,
            nextHopProtocol = map["nextHopProtocol"] as String,
            workerStart = parseTimestamp(map["workerStart"]),
            redirectStart = parseTimestamp(map["redirectStart"]),
            redirectEnd = parseTimestamp(map["redirectEnd"]),
            fetchStart = parseTimestamp(map["fetchStart"]),
            domainLookupStart = parseTimestamp(map["domainLookupStart"]),
            domainLookupEnd = parseTimestamp(map["domainLookupEnd"]),
            connectStart = parseTimestamp(map["connectStart"]),
            connectEnd = parseTimestamp(map["connectEnd"]),
            secureConnectionStart = parseTimestamp(map["secureConnectionStart"]),
            requestStart = parseTimestamp(map["requestStart"]),
            responseStart = parseTimestamp(map["responseStart"]),
            responseEnd = parseTimestamp(map["responseEnd"]),
            transferSize = map["transferSize"] as Long,
            encodedBodySize = map["encodedBodySize"] as Long,
            decodedBodySize = map["decodedBodySize"] as Long
        )
    }

    private fun parsePerformanceEntry(
        map: Any?
    ): PerformanceEntry {
        if (map !is Map<*, *>) {
            throw Exception("Unexpected non-map JavaScript value: $map")
        }
        return PerformanceEntry(
            name = map["name"] as String,
            entryType = map["entryType"] as String,
            startTime = parseTimestamp(map["startTime"]),
            duration = parseTimestamp(map["duration"])
        )
    }

    private fun parseTimestamp(
        timestamp: Any?
    ): Duration = when (timestamp) {
        is Long -> Duration.ofMillis(timestamp)
        is Double -> Duration.ofMillis(timestamp.toLong())
        else -> throw Exception("Cannot parse timestamp from $timestamp")
    }

    private fun parseNavigationType(
        jsNavigationType: Any?
    ): NavigationType {
        jsNavigationType as String
        return NavigationType.valueOf(jsNavigationType.toUpperCase())
    }
}
