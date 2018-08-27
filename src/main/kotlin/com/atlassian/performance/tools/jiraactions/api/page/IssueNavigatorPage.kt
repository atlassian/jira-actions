package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.page.JiraErrors
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class IssueNavigatorPage(
    private val driver: WebDriver,
    val jql: String
) {

    /**
     * Requires an issue detail pane to be visible.
     * For example, if there are no issues found, this will time out.
     */
    fun waitForIssueNavigator(): IssueNavigatorPage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            Duration.ofSeconds(30),
            ExpectedConditions.and(
                ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("ol.issue-list")),
                    ExpectedConditions.presenceOfElementLocated(By.id("issuetable")),
                    ExpectedConditions.presenceOfElementLocated(By.id("issue-content")),
                    ExpectedConditions.presenceOfElementLocated(By.className("no-results-hint")),
                    jiraErrors.anyCommonError()
                ),
                ExpectedConditions.presenceOfElementLocated(By.id("key-val")),
                ExpectedConditions.presenceOfElementLocated(By.className("issue-body-content"))
            )
        )
        return this
    }

    fun getIssueKeys(): Set<String> {
        return driver
            .findElements(By.className("issue-link-key"))
            .map { it.text }
            .map { it.trim() }
            .toSet()
    }

    fun issueView(): IssuePage {
        return IssuePage(driver)
    }

    fun selectedIssueId(): Long {
        return IssuePage(driver).getIssueId()
    }

    fun getTotalResults(): Int = driver.findElement(By.className("showing")).text.trim().substringAfter("of ").toInt()
}