package com.atlassian.jira.test.performance.actions.memories

import com.atlassian.jira.test.performance.actions.page.IssuePage

interface JqlMemory : Memory<String> {
    fun observe(issuePage: IssuePage)
}