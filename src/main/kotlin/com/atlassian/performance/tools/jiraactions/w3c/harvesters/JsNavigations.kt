package com.atlassian.performance.tools.jiraactions.w3c.harvesters

import com.atlassian.performance.tools.jiraactions.api.w3c.NavigationType
import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceNavigationTiming
import org.openqa.selenium.JavascriptExecutor
import java.util.function.Supplier

internal fun getJsNavigationsPerformance(
    jsExecutor: JavascriptExecutor,
    nodeIdSupplier: Supplier<String?>
): List<PerformanceNavigationTiming> {
    val jsNavigations = jsExecutor.executeScript("return window.performance.getEntriesByType(\"navigation\");")
    return parseNavigations(jsNavigations, nodeIdSupplier)
}

private fun parseNavigations(
    jsNavigations: Any?,
    nodeIdSupplier: Supplier<String?>
): List<PerformanceNavigationTiming> {
    if (jsNavigations !is List<*>) {
        throw Exception("Unexpected non-list JavaScript value: $jsNavigations")
    }
    return jsNavigations.map { parsePerformanceNavigationTiming(it, nodeIdSupplier) }
}

private fun parsePerformanceNavigationTiming(
    map: Any?,
    nodeIdSupplier: Supplier<String?>
): PerformanceNavigationTiming {
    if (map !is Map<*, *>) {
        throw Exception("Unexpected non-map JavaScript value: $map")
    }
    return PerformanceNavigationTiming(
        resource = parsePerformanceResourceTiming(map, nodeIdSupplier),
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
