package com.atlassian.performance.tools.jiraactions.api.w3c

import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsElementsPerformance
import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsNavigationsPerformance
import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsResourcesPerformance
import com.atlassian.performance.tools.jiraactions.w3c.harvesters.parseInstantMilli
import org.openqa.selenium.JavascriptExecutor
import java.time.Instant

/**
 * Obtains entries from [javascript].
 */
class JavascriptW3cPerformanceTimeline private constructor(
    private val javascript: JavascriptExecutor,
    private val recordNavigation: Boolean,
    private val recordResources: Boolean,
    private val recordElements: Boolean
) : W3cPerformanceTimeline {

    @Deprecated("Use JavascriptW3cPerformanceTimeline.Builder instead.")
    constructor(
        javascript: JavascriptExecutor
    ) : this(javascript, true, true, false)

    override fun record(): RecordedPerformanceEntries {
        return RecordedPerformanceEntries(
            navigations = if (recordNavigation) getJsNavigationsPerformance(javascript) else emptyList(),
            resources = if (recordResources) getJsResourcesPerformance(javascript) else emptyList(),
            elements = if (recordElements) getJsElementsPerformance(javascript) else emptyList(),
            timeOrigin = getTimeOrigin(javascript)
        )
    }

    private fun getTimeOrigin(javascript: JavascriptExecutor): Instant {
        return parseInstantMilli(javascript.executeScript("return window.performance.timeOrigin;"))
    }

    class Builder(
        private var javascript: JavascriptExecutor
    ) {
        private var recordNavigation = true
        private var recordResources = true
        private var recordElements = false

        fun javascript(javascript: JavascriptExecutor) = apply { this.javascript = javascript }
        fun recordNavigation(recordNavigation: Boolean) = apply { this.recordNavigation = recordNavigation }
        fun recordResources(recordResources: Boolean) = apply { this.recordResources = recordResources }
        fun recordElements(recordElements: Boolean) = apply { this.recordElements = recordElements }
        fun recordAll() = apply {
            recordNavigation = true
            recordResources = true
            recordElements = true
        }

        fun build(): W3cPerformanceTimeline = JavascriptW3cPerformanceTimeline(
            javascript = javascript,
            recordNavigation = recordNavigation,
            recordResources = recordResources,
            recordElements = recordElements
        )
    }
}
