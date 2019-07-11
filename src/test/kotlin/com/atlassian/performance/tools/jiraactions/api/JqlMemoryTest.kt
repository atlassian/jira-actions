package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage
import com.atlassian.performance.tools.jiraactions.jql.BuiltInJQL
import org.assertj.core.api.Assertions
import org.junit.Test
import org.mockito.Mockito
import org.mockito.stubbing.Answer
import org.openqa.selenium.WebDriver

class JqlMemoryTest {

    @Test
    fun expectPrioritiesJQL() {
        val memory: JqlMemory = AdaptiveJqlMemory(SeededRandom());
        val webdriverMock = Mockito.mock(WebDriver::class.java)
        val issuePage: IssuePage = IssuePage(webdriverMock)
        val issuePageSpy = Mockito.spy(issuePage)
        Mockito.doAnswer(Answer { listOf("SMALL", "MIDDLE", "BIG", "HUGE") })
            .`when`(issuePageSpy)
            .getPossiblePriorities()
        Mockito.doAnswer(Answer { null })
            .`when`(issuePageSpy)
            .getAssignee()
        Mockito.doAnswer(Answer { null })
            .`when`(issuePageSpy).getProject()
        Mockito.doAnswer(Answer { null })
            .`when`(issuePageSpy)
            .visitReporterProfile()

        memory.observe(issuePageSpy)
        val jql = memory.recall { s -> BuiltInJQL.PRIORITIES.name == s }

        Assertions.assertThat(jql)
            .isNotNull()
            .startsWith("priority in (")
            .endsWith(") order by reporter")
    }
}