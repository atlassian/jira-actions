package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.and
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.presenceOfElementLocated
import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.time.Duration

open class IssueNavigatorPage(
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
            or(
                and(
                    or(
                        presenceOfElementLocated(By.cssSelector("ol.issue-list")),
                        presenceOfElementLocated(By.id("issuetable")),
                        presenceOfElementLocated(By.id("issue-content"))
                    ),
                    presenceOfElementLocated(By.id("key-val")),
                    presenceOfElementLocated(By.className("issue-body-content"))
                ),
                presenceOfElementLocated(By.className("no-results-hint")),
                jiraErrors.anyCommonErrorNative()
            )
        )
        return this
    }

    fun getIssueKeys(): Set<String> {
        val issueKeys: List<String> = JavaScriptUtils.executeScript(driver,
            "return Array.from(document.getElementsByClassName('issue-link-key'), i => i.innerText.trim())"
        )

        return HashSet(issueKeys)
    }

    fun issueView(): IssuePage {
        return IssuePage(driver)
    }

    fun selectedIssueId(): Long {
        return IssuePage(driver).getIssueId()
    }

    fun getTotalResults(): Int = driver
        .findElements(By.className("showing"))
        .singleOrNull()
        ?.text
        ?.trim()
        ?.substringAfter("of ")
        ?.toInt() ?: 0
}
