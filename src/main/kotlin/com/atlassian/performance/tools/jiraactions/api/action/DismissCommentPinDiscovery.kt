package com.atlassian.performance.tools.jiraactions.api.action

import com.atlassian.performance.tools.jiraactions.page.scrollIntoView
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class DismissCommentPinDiscovery(
    private val driver: WebDriver
) : Action {

    override fun run() {
        driver
            .findElements(By.tagName("jira-comment-pins-discovery")).firstOrNull()
            ?.findElements(By.xpath("//*[contains(text(), 'Got it')]"))?.firstOrNull()
            ?.scrollIntoView(driver)
            ?.click()
    }
}
