package com.atlassian.performance.tools.jiraactions.api.memories.adaptive

import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory

/**
 * `JqlMemory` decorator that limits number of memories delegated via `remember` to only the first entries passed.
 * Only `limit` number of memories is remembered.
 */
class LimitedJqlMemory(
    private val delegate: JqlMemory,
    private val limit: Int
) : JqlMemory by delegate {
    private var numberOfRemembered = 0

    override fun remember(
        memories: Collection<String>
    ) = delegate
        .remember(
            memories = memories.take(limit - numberOfRemembered)
                .also { numberOfRemembered += it.size }
        )
}
