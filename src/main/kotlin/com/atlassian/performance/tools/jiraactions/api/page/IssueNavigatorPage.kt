package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.seleniumjs.NativeExpectedCondition
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.and
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.presenceOfElementLocated
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorView.*
import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import java.time.Duration

open class IssueNavigatorPage(
    private val driver: WebDriver,
    val jql: String
) {
    private val emptyResults = presenceOfElementLocated(By.className("no-results-hint"))

    fun waitForIssueNavigator(): IssueNavigatorPage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            Duration.ofSeconds(10),
            or(
                DETAIL_VIEW.resultsPresent,
                LIST_VIEW.resultsPresent,
                emptyResults,
                jiraErrors.anyCommonErrorNative()
            )
        )
        return this
    }

    fun switchTo(view: IssueNavigatorView): IssueNavigatorPage {
        val currentView = getCurrentView()
        if (currentView != view) {
            driver.wait(elementToBeClickable(By.id("layout-switcher-button"))).click()
            driver.wait(elementToBeClickable(By.cssSelector("a[data-layout-key=${view.selector}]"))).click()
        }
        return this
    }

    private fun getCurrentView(): IssueNavigatorView {
        driver.wait(Duration.ofSeconds(10), presenceOfElementLocated(By.className("results-panel")))
        return if (driver.isElementPresent(By.cssSelector("div.list-view"))) {
            LIST_VIEW
        } else {
            DETAIL_VIEW
        }
    }

    open fun getIssueKeys(): Set<String> {
        val issueKeys: List<String> = JavaScriptUtils.executeScript(
            driver,
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

    protected fun getDriver(): WebDriver {
        return this.driver
    }

    fun getTotalResults(): Int = driver
        .findElements(By.className("showing"))
        .singleOrNull()
        ?.text
        ?.trim()
        ?.substringAfter("of ")
        ?.toInt() ?: 0
}

enum class IssueNavigatorView(
    val selector: String,
    val resultsPresent: NativeExpectedCondition
) {
    LIST_VIEW(
        "list-view",
        and(
            presenceOfElementLocated(By.id("issuetable")),
            presenceOfElementLocated(By.cssSelector(".issuerow.focused"))
        )
    ),
    DETAIL_VIEW(
        "split-view",
        and(
            or(
                presenceOfElementLocated(By.cssSelector("ol.issue-list")),
                presenceOfElementLocated(By.id("issuetable")),
                presenceOfElementLocated(By.id("issue-content"))
            ),
            presenceOfElementLocated(By.id("key-val")),
            presenceOfElementLocated(By.className("issue-body-content"))
        )
    )
}
