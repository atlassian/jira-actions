package com.atlassian.performance.tools.jiraactions.api.w3c

import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsElementsPerformance
import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsNavigationsPerformance
import com.atlassian.performance.tools.jiraactions.w3c.harvesters.getJsResourcesPerformance
import org.openqa.selenium.JavascriptExecutor
import java.util.function.Supplier

/**
 * Obtains entries from [javascript].
 */
class JavascriptW3cPerformanceTimeline private constructor(
    private val javascript: JavascriptExecutor,
    private val recordNavigation: Boolean,
    private val recordResources: Boolean,
    private val recordElements: Boolean,
    private val nodeIdSupplier: Supplier<String?>
) : W3cPerformanceTimeline {

    @Deprecated("Use JavascriptW3cPerformanceTimeline.Builder instead.")
    constructor(
        javascript: JavascriptExecutor
    ) : this(javascript, true, true, false, Supplier { null })

    override fun record(): RecordedPerformanceEntries {
        return RecordedPerformanceEntries(
            navigations = if (recordNavigation) getJsNavigationsPerformance(
                javascript,
                nodeIdSupplier
            ) else emptyList(),
            resources = if (recordResources) getJsResourcesPerformance(javascript, nodeIdSupplier) else emptyList(),
            elements = if (recordElements) getJsElementsPerformance(javascript) else emptyList()
        )
    }

    class Builder(
        private var javascript: JavascriptExecutor
    ) {
        private var recordNavigation = true
        private var recordResources = true
        private var recordElements = false
        private var nodeIdSupplier: Supplier<String?> = Supplier { null }

        fun javascript(javascript: JavascriptExecutor) = apply { this.javascript = javascript }
        fun recordNavigation(recordNavigation: Boolean) = apply { this.recordNavigation = recordNavigation }
        fun recordResources(recordResources: Boolean) = apply { this.recordResources = recordResources }
        fun recordElements(recordElements: Boolean) = apply { this.recordElements = recordElements }

        fun nodeIdSupplier(nodeIdSupplier: Supplier<String?>) = apply { this.nodeIdSupplier = nodeIdSupplier }
        fun recordAll() = apply {
            recordNavigation = true
            recordResources = true
            recordElements = true
        }

        fun build(): W3cPerformanceTimeline = JavascriptW3cPerformanceTimeline(
            javascript = javascript,
            recordNavigation = recordNavigation,
            recordResources = recordResources,
            recordElements = recordElements,
            nodeIdSupplier = nodeIdSupplier
        )
    }
}
