package com.atlassian.performance.tools.jiraactions.w3c.harvesters

import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceEntry
import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceResourceTiming
import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceServerTiming
import org.openqa.selenium.JavascriptExecutor

internal fun getJsResourcesPerformance(javascript: JavascriptExecutor): List<PerformanceResourceTiming> {
    val jsResources = javascript.executeScript("return window.performance.getEntriesByType(\"resource\");")
    return parseResources(jsResources)
}

private fun parseResources(
    jsResources: Any
): List<PerformanceResourceTiming> {
    if (jsResources !is List<*>) {
        throw Exception("Unexpected non-list JavaScript value: $jsResources")
    }
    return jsResources.map { parsePerformanceResourceTiming(it) }
}

internal fun parsePerformanceResourceTiming(
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
        decodedBodySize = map["decodedBodySize"] as Long,
        serverTiming = map["serverTiming"]?.let { parseServerTimings(it) }
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

private fun parseServerTimings(
    jsServerTimings: Any
): List<PerformanceServerTiming> {
    if (jsServerTimings !is List<*>) {
        throw Exception("Unexpected non-list JavaScript value: $jsServerTimings")
    }
    return jsServerTimings.map { parseServerTiming(it) }
}

private fun parseServerTiming(
    map: Any?
): PerformanceServerTiming {
    if (map !is Map<*, *>) {
        throw Exception("Unexpected non-map JavaScript value: $map")
    }
    return PerformanceServerTiming(
        name = map["name"] as String,
        duration = parseTimestamp(map["duration"]),
        description = map["description"] as String
    )
}
