package com.atlassian.performance.tools.jiraactions.api.memories

import com.atlassian.performance.tools.jiraactions.api.page.IssuePage

interface JqlMemory : Memory<String> {
    fun observe(issuePage: IssuePage)
}