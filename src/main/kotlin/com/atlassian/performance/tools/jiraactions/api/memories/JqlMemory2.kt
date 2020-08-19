package com.atlassian.performance.tools.jiraactions.api.memories

import com.atlassian.performance.tools.jiraactions.api.jql.Jql
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage
import java.util.function.Predicate

interface JqlMemory2 : Memory<Jql> {

    fun observe(issuePage: IssuePage)
    fun recall(filter: Predicate<Jql>): Jql?

}
