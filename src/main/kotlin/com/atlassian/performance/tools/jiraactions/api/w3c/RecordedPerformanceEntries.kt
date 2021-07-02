package com.atlassian.performance.tools.jiraactions.api.w3c

/**
 * Holds interesting recorded performance entries.
 * They share the same timeline and can be cross-examined.
 */
class RecordedPerformanceEntries internal constructor(
    val navigations: List<PerformanceNavigationTiming>,
    val resources: List<PerformanceResourceTiming>,
    val elements: List<PerformanceElementTiming>
)
