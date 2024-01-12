package com.atlassian.performance.tools.jiraactions.api.w3c

import java.time.Instant

/**
 * Holds interesting recorded performance entries.
 * They share the same timeline and can be cross-examined.
 */
class RecordedPerformanceEntries internal constructor(
    val navigations: List<PerformanceNavigationTiming>,
    val resources: List<PerformanceResourceTiming>,
    val elements: List<PerformanceElementTiming>,
    /**
     * @since 3.27.0
     */
    val timeOrigin: Instant?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordedPerformanceEntries

        if (navigations != other.navigations) return false
        if (resources != other.resources) return false
        if (elements != other.elements) return false

        return true
    }

    override fun hashCode(): Int {
        var result = navigations.hashCode()
        result = 31 * result + resources.hashCode()
        result = 31 * result + elements.hashCode()
        return result
    }

    override fun toString(): String {
        return "RecordedPerformanceEntries(navigations=$navigations, resources=$resources, elements=$elements)"
    }

}
