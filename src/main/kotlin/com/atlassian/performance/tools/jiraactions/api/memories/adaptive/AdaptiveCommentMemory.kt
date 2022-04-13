package com.atlassian.performance.tools.jiraactions.api.memories.adaptive

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.memories.Comment
import com.atlassian.performance.tools.jiraactions.api.memories.CommentMemory

class AdaptiveCommentMemory(
    private val random: SeededRandom
) : CommentMemory {
    private val comments = mutableSetOf<Comment>()

    override fun recall(): Comment? {
        if (comments.isEmpty()) {
            return null
        }
        return random.pick(comments.toList())
    }

    override fun remember(memories: Collection<Comment>) {
        comments.addAll(memories)
    }
}
