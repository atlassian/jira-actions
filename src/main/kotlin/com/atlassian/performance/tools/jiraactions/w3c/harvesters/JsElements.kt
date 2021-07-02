package com.atlassian.performance.tools.jiraactions.w3c.harvesters

import com.atlassian.performance.tools.jiraactions.api.w3c.PerformanceElementTiming
import org.openqa.selenium.JavascriptExecutor


internal fun getJsElementsPerformance(javascript: JavascriptExecutor): List<PerformanceElementTiming> {
    val jsElements =
        javascript.executeScript(
            "const observer = new PerformanceObserver(() => {});" +
                "observer.observe({type: 'element', buffered: true});" +
                "return observer.takeRecords()"
        )
    return parseElements(jsElements)
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
