package com.atlassian.performance.tools.jiraactions.memories

import com.atlassian.performance.tools.jiraactions.page.IssuePage

interface JqlMemory : Memory<String> {
    fun observe(issuePage: IssuePage)
}