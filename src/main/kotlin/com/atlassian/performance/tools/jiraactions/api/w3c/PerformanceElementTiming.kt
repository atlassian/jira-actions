package com.atlassian.performance.tools.jiraactions.api.w3c

import java.time.Duration


/**
 * Represents the [PerformanceElementTiming](https://wicg.github.io/element-timing/#performanceelementtiming).
 */
class PerformanceElementTiming internal constructor(
    val renderTime: Duration,
    val loadTime: Duration,
    val identifier: String,
    val naturalWidth: Long,
    val naturalHeight: Long,
    val id: String,
    val url: String
)
