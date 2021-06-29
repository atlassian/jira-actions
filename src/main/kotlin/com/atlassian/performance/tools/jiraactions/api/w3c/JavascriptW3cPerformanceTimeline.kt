package com.atlassian.performance.tools.jiraactions.api.w3c

import com.atlassian.performance.tools.jiraactions.api.w3c.harvesters.getJsElementsPerformance
import com.atlassian.performance.tools.jiraactions.api.w3c.harvesters.getJsNavigationsPerformance
import com.atlassian.performance.tools.jiraactions.api.w3c.harvesters.getJsResourcesPerformance
import org.openqa.selenium.JavascriptExecutor

/**
 * Obtains entries from [javascript].
 */
class JavascriptW3cPerformanceTimeline(
    javascript: JavascriptExecutor,
    withNavigationPerformance: Boolean = true,
    withResourcesPerformance: Boolean = true,
    withElementPerformance: Boolean = true
) : W3cPerformanceTimeline {
    private var getNavigationPerformance: () -> List<PerformanceNavigationTiming> =
        if (withNavigationPerformance) ({ getJsNavigationsPerformance(javascript) }) else ({ emptyList() })
    private var getResourcesPerformance: () -> List<PerformanceResourceTiming> =
        if (withResourcesPerformance) ({ getJsResourcesPerformance(javascript) }) else ({ emptyList() })
    private var getElementPerformance: () -> List<PerformanceElementTiming> =
        if (withElementPerformance) ({ getJsElementsPerformance(javascript) }) else ({ emptyList() })

    override fun record(): RecordedPerformanceEntries? {
        return RecordedPerformanceEntries(
            navigations = getNavigationPerformance(),
            resources = getResourcesPerformance(),
            elements = getElementPerformance()
        )
    }
}
