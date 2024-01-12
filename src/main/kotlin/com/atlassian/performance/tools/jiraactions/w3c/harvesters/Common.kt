package com.atlassian.performance.tools.jiraactions.w3c.harvesters

import java.time.Duration
import java.time.Instant
import kotlin.math.floor

internal fun parseTimestamp(
    timestamp: Any?
): Duration = when (timestamp) {
    is Long -> Duration.ofMillis(timestamp)
    is Double -> Duration.ofMillis(timestamp.toLong())
    else -> throw Exception("Cannot parse timestamp from $timestamp")
}

internal fun parseInstantMilli(
    milli: Any?
): Instant = when (milli) {
    is Long -> Instant.ofEpochMilli(milli)
    is Double -> {
        val wholeMilli: Long = floor(milli).toLong()
        val remainderMilli: Double = milli - wholeMilli
        val remainderNanos: Long = (remainderMilli * 1_000_000).toLong()
        Instant.ofEpochMilli(wholeMilli).plusNanos(remainderNanos)
    }

    else -> throw Exception("Cannot parse instant from $milli")
}
