package com.atlassian.performance.tools.jiraactions.w3c.harvesters

import com.atlassian.performance.tools.jiraactions.api.w3c.NavigationType
import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceNavigationTiming
import org.openqa.selenium.JavascriptExecutor

internal fun getJsNavigationsPerformance(jsExecutor: JavascriptExecutor): List<PerformanceNavigationTiming> {
    val jsNavigations = jsExecutor.executeScript("return window.performance.getEntriesByType(\"navigation\");")
    return parseNavigations(jsNavigations)
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

private fun parseNavigationType(
    jsNavigationType: Any?
): NavigationType {
    jsNavigationType as String
    return NavigationType.valueOf(jsNavigationType.toUpperCase())
}
