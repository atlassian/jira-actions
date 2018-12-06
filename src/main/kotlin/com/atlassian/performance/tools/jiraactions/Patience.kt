package com.atlassian.performance.tools.jiraactions

import java.time.Duration
import java.time.Instant

internal class Patience(
    private val timeout: Duration = Duration.ofSeconds(10)
) {
    fun test(
        condition: () -> Boolean
    ): Boolean {
        val start = Instant.now()
        val deadline = start.plus(timeout)
        while (true) {
            if (condition.invoke()) {
                return true
            }
            if (Instant.now().isAfter(deadline)) {
                return false
            }
            Thread.sleep(50)
        }
    }
}