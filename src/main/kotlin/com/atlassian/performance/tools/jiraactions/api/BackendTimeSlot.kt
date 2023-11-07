package com.atlassian.performance.tools.jiraactions.api

import java.time.Duration
import java.time.Instant

class BackendTimeSlot internal constructor(
    val start: Instant,
    val end: Instant,
    val threadId: Long,
    val nodeId: String
) {
    val duration: Duration = Duration.between(start, end)

    fun contains(instant: Instant): Boolean = instant.isAfter(start) && instant.isBefore(end)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BackendTimeSlot

        if (start != other.start) return false
        if (end != other.end) return false
        if (threadId != other.threadId) return false
        if (nodeId != other.nodeId) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + threadId.hashCode()
        result = 31 * result + (nodeId?.hashCode() ?: 0)
        result = 31 * result + duration.hashCode()
        return result
    }

    override fun toString(): String {
        return "BackendTimeSlot(start=$start, end=$end, threadId=$threadId, nodeId=$nodeId, duration=$duration)"
    }


}
