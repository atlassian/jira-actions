package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.and
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.presenceOfElementLocated
import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import java.time.Duration

open class IssueNavigatorPage(
    private val driver: WebDriver,
    val jql: String
) {
    private val detailView = and(
        or(
            presenceOfElementLocated(By.cssSelector("ol.issue-list")),
            presenceOfElementLocated(By.id("issuetable")),
            presenceOfElementLocated(By.id("issue-content"))
        ),
        presenceOfElementLocated(By.id("key-val")),
        presenceOfElementLocated(By.className("issue-body-content"))
    )
    private val listView = and(
        presenceOfElementLocated(By.id("issuetable")),
        presenceOfElementLocated(By.cssSelector(".issuerow.focused"))
    )
    private val emptyResults = presenceOfElementLocated(By.className("no-results-hint"))

    fun waitForIssueNavigator(): IssueNavigatorPage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            Duration.ofSeconds(10),
            or(
                detailView,
                listView,
                emptyResults,
                jiraErrors.anyCommonErrorNative()
            )
        )
        return this
    }

    fun switchLayoutTo(view: IssueNavigatorView): IssueNavigatorPage {
        val currentLayout = getCurrentLayout()
        if (currentLayout != view) {
            driver.wait(elementToBeClickable(By.id("layout-switcher-button"))).click()
            driver.wait(elementToBeClickable(By.cssSelector("a[data-layout-key=${view.selector}]"))).click()
        }
        return this
    }

    private fun getCurrentLayout(): IssueNavigatorView {
        driver.wait(Duration.ofSeconds(10), presenceOfElementLocated(By.className("results-panel")))
        return if (driver.isElementPresent(By.cssSelector("div.list-view"))) {
            IssueNavigatorView.LIST_VIEW
        } else {
            IssueNavigatorView.DETAIL_VIEW
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

enum class IssueNavigatorView(val selector: String) {
    LIST_VIEW("list-view"),
    DETAIL_VIEW("split-view")
}
