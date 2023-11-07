package com.atlassian.performance.tools.jiraactions.api

import java.time.Duration
import java.time.Instant

data class BackendTimeSlot internal constructor(
    val start: Instant,
    val end: Instant,
    val threadId: Long,
    val nodeId: String
) {
    val duration: Duration = Duration.between(start, end)

    fun contains(instant: Instant): Boolean = instant.isAfter(start) && instant.isBefore(end)
}
