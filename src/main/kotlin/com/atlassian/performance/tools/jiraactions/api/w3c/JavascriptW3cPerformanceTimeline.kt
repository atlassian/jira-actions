package com.atlassian.performance.tools.jiraactions.api.w3c

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.TimeoutException
import java.time.Duration

/**
 * Obtains entries from [javascript].
 */
class JavascriptW3cPerformanceTimeline(
    private val javascript: JavascriptExecutor
) : W3cPerformanceTimeline {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun record(): RecordedPerformanceEntries? {
        val jsResources = javascript.executeScript("return window.performance.getEntriesByType(\"resource\");")
        val jsNavigations = javascript.executeScript("return window.performance.getEntriesByType(\"navigation\");")
        val jsElements =
            getJSElementPerformance()
        return RecordedPerformanceEntries(
            navigations = parseNavigations(jsNavigations),
            resources = parseResources(jsResources),
            elements = parseElements(jsElements)
        )
    }

    private fun getJSElementPerformance(): Any? {
        return try {
            javascript.executeAsyncScript("new PerformanceObserver(list => arguments[0](list.getEntries())).observe({type: 'element', buffered: true})")
        } catch (timeoutEx: TimeoutException) {
            logger.info("No element performance data acquired.")
            null
        }
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

    private fun parseElements(
        jsElements: Any?
    ): List<PerformanceElementTiming> {
        if (jsElements !is List<*>) {
            throw Exception("Unexpected non-list JavaScript value: $jsElements")
        }
        return jsElements.map { parsePerformanceElementTiming(it) }
    }

    private fun parsePerformanceElementTiming(
        map: Any?
    ): PerformanceElementTiming {
        if (map !is Map<*, *>) {
            throw Exception("Unexpected non-map JavaScript value: $map")
        }
        return PerformanceElementTiming(
            renderTime = parseTimestamp(map["renderTime"]),
            loadTime = parseTimestamp(map["loadTime"]),
            identifier = map["identifier"] as String,
            naturalWidth = map["naturalWidth"] as Long,
            naturalHeight = map["naturalHeight"] as Long,
            id = map["id"] as String,
            url = map["url"] as String
        )
    }
}
