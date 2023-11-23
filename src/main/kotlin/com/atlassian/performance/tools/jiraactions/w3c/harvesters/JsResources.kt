package com.atlassian.performance.tools.jiraactions.w3c.harvesters

import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceEntry
import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceResourceTiming
import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceServerTiming
import org.openqa.selenium.JavascriptExecutor
import java.time.Duration
import java.util.function.Supplier

internal fun getJsResourcesPerformance(
    jsExecutor: JavascriptExecutor,
    nodeIdSupplier: Supplier<String?>
): List<PerformanceResourceTiming> {
    val jsResources = jsExecutor.executeScript("return window.performance.getEntriesByType(\"resource\");")
    return parseResources(jsResources, nodeIdSupplier)
}

private fun parseResources(
    jsResources: Any,
    nodeIdSupplier: Supplier<String?>
): List<PerformanceResourceTiming> {
    if (jsResources !is List<*>) {
        throw Exception("Unexpected non-list JavaScript value: $jsResources")
    }
    return jsResources.map { parsePerformanceResourceTiming(it, nodeIdSupplier) }
}

internal fun parsePerformanceResourceTiming(
    map: Any?,
    nodeIdSupplier: Supplier<String?>
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
        serverTiming = map["serverTiming"]?.let { parseServerTimings(it, nodeIdSupplier) }
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
    jsServerTimings: Any,
    nodeIdSupplier: Supplier<String?>
): List<PerformanceServerTiming> {
    if (jsServerTimings !is List<*>) {
        throw Exception("Unexpected non-list JavaScript value: $jsServerTimings")
    }
    val result = jsServerTimings.map { parseServerTiming(it) }.toMutableList()
    val nodeId = nodeIdSupplier.get()
    if (nodeId != null) {
        addAttribute(result, "nodeId", nodeId)
    }
    return result
}

private fun addAttribute(result: MutableList<PerformanceServerTiming>, attributeName: String, attributeValue: String?) {
    if (attributeValue != null) {
        result.add(
            PerformanceServerTiming(
                name = attributeName,
                duration = Duration.ZERO,
                description = attributeValue
            )
        )
    }
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

private fun getResponseHeaders(jsExecutor: JavascriptExecutor): Map<String, String?> {
    val jsResult = jsExecutor.executeScript(
        """
                var r = new XMLHttpRequest();
                r.open('HEAD', window.location, false);
                r.send(null);
                var headersString = r.getAllResponseHeaders();
                const arr = headersString.trim().split(/[\r\n]+/);
                const headerMap = {};
                arr.forEach((line) => {
                  const parts = line.split(": ");
                  const header = parts.shift();
                  const value = parts.join(": ");
                  headerMap[header] = value;
                });
                return headerMap;
        """.trimIndent()
    )
    @Suppress("UNCHECKED_CAST")
    return (jsResult as Map<String, String>).mapKeys { it.key.toLowerCase() }
}
