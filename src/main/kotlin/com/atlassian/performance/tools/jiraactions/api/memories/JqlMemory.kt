package com.atlassian.performance.tools.jiraactions.api.memories

import com.atlassian.performance.tools.jiraactions.api.page.IssuePage
import java.util.function.Predicate

interface JqlMemory : Memory<String> {

    fun observe(issuePage: IssuePage)

    @JvmDefault
    fun recallByTag(filter: Predicate<String>): String? {
        return null
    }
}