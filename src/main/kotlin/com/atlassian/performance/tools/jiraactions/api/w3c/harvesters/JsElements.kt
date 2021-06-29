package com.atlassian.performance.tools.jiraactions.api.w3c.harvesters

import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceElementTiming
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.TimeoutException


internal fun getJsElementsPerformance(javascript: JavascriptExecutor): List<PerformanceElementTiming> {
    return try {
        val jsElements =
            javascript.executeAsyncScript("new PerformanceObserver(list => arguments[0](list.getEntries())).observe({type: 'element', buffered: true})")
        parseElements(jsElements)
    } catch (timeoutEx: TimeoutException) {
        // No element performance entries on page.
        emptyList()
    }
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
