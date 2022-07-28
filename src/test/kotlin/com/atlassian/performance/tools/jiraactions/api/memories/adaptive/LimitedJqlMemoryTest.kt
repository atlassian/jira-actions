package com.atlassian.performance.tools.jiraactions.api.memories.adaptive

import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class LimitedJqlMemoryTest {

    @Test
    fun delegatesOnly2FirstMemoriesToRememberIfLimitSetTo2() {
        val delegate = JqlMemoryMock()
        val memory = LimitedJqlMemory(
            delegate = delegate,
            limit = 2
        )

        memory.remember(
            memories = listOf(
                "First JQL",
                "Second JQL",
                "Third JQL"
            )
        )
        memory.remember(
            memories = listOf(
                "Fourth JQL",
                "Fifth JQL",
                "Sixth JQL"
            )
        )

        val remembered = delegate.getRememberedMemories().flatten()
        assertThat(remembered.size, equalTo(2))
        assertThat(
            remembered,
            equalTo(listOf(
                "First JQL",
                "Second JQL"
            ))
        )
    }


    @Test
    fun delegatesOnly5FirstMemoriesToRememberIfLimitSetTo5() {
        val delegate = JqlMemoryMock()
        val memory = LimitedJqlMemory(
            delegate = delegate,
            limit = 5
        )

        memory.remember(
            memories = listOf(
                "First JQL",
                "Second JQL",
                "Third JQL"
            )
        )
        memory.remember(
            memories = listOf(
                "Fourth JQL",
                "Fifth JQL",
                "Sixth JQL"
            )
        )

        val remembered = delegate.getRememberedMemories().flatten()
        assertThat(remembered.size, equalTo(5))
        assertThat(
            remembered,
            equalTo(listOf(
                "First JQL",
                "Second JQL",
                "Third JQL",
                "Fourth JQL",
                "Fifth JQL"
            ))
        )
    }

    private class JqlMemoryMock : JqlMemory {
        private val rememberedMemories = mutableListOf<Collection<String>>()

        override fun observe(issuePage: IssuePage) { }
        override fun recall(): String? = null
        override fun remember(memories: Collection<String>) { rememberedMemories.add(memories) }

        fun getRememberedMemories() = rememberedMemories.toList()
    }
}
