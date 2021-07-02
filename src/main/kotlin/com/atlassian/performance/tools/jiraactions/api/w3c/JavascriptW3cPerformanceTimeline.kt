package com.atlassian.performance.tools.jiraactions.api.w3c

import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsElementsPerformance
import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsNavigationsPerformance
import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsResourcesPerformance
import org.openqa.selenium.JavascriptExecutor

/**
 * Obtains entries from [javascript].
 */
class JavascriptW3cPerformanceTimeline private constructor(
    private val javascript: JavascriptExecutor,
    private val withNavigationPerformance: Boolean,
    private val withResourcesPerformance: Boolean,
    private val withElementPerformance: Boolean
) : W3cPerformanceTimeline {

    @Deprecated("Use JavascriptW3cPerformanceTimeline.Builder instead.")
    constructor(
        javascript: JavascriptExecutor
    ) : this(javascript, true, true, true)

    override fun record(): RecordedPerformanceEntries {
        return RecordedPerformanceEntries(
            navigations = if (withNavigationPerformance) getJsNavigationsPerformance(javascript) else emptyList(),
            resources = if (withResourcesPerformance) getJsResourcesPerformance(javascript) else emptyList(),
            elements = if (withElementPerformance) getJsElementsPerformance(javascript) else emptyList()
        )
    }

    class Builder(private val javascript: JavascriptExecutor) {
        private var navigationPerformance = false
        private var resourcesPerformance = false
        private var elementPerformance = false

        fun withNavigationPerformance() = apply { navigationPerformance = true }
        fun withResourcesPerformance() = apply { resourcesPerformance = true }
        fun withElementPerformance() = apply { elementPerformance = true }
        fun withAllMetrics() = apply {
            navigationPerformance = true
            resourcesPerformance = true
            elementPerformance = true
        }

        fun build() = JavascriptW3cPerformanceTimeline(
            javascript = javascript,
            withNavigationPerformance = navigationPerformance,
            withResourcesPerformance = resourcesPerformance,
            withElementPerformance = elementPerformance
        )
    }
}
