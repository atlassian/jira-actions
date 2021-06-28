package com.atlassian.performance.tools.jiraactions.w3c.harvesters

import java.time.Duration

internal fun parseTimestamp(
    timestamp: Any?
): Duration = when (timestamp) {
    is Long -> Duration.ofMillis(timestamp)
    is Double -> Duration.ofMillis(timestamp.toLong())
    else -> throw Exception("Cannot parse timestamp from $timestamp")
}
