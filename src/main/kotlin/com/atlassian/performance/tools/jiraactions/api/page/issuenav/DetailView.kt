package com.atlassian.performance.tools.jiraactions.api.page.issuenav

import com.atlassian.performance.seleniumjs.NativeExpectedCondition
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.and
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.presenceOfElementLocated
import com.atlassian.performance.tools.jiraactions.api.page.isElementPresent
import com.atlassian.performance.tools.jiraactions.api.page.wait
import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import java.time.Duration

class DetailView(
    private val driver: WebDriver
) : IssueNavResultsView {

    private fun isSelected(): Boolean {
        driver.wait(Duration.ofSeconds(10), presenceOfElementLocated(By.className("results-panel")))
        return driver.isElementPresent(By.className("details-layout"))
    }

    override fun switchToView() {
        if (!isSelected()) {
            driver.wait(elementToBeClickable(By.id("layout-switcher-button"))).click()
            driver.wait(elementToBeClickable(By.cssSelector("[data-layout-key=split-view]"))).click()
        }
    }

    override fun detectResults(): NativeExpectedCondition = and(
        or(
            presenceOfElementLocated(By.className("issue-list")),
            presenceOfElementLocated(By.id("issuetable")),
            presenceOfElementLocated(By.id("issue-content"))
        ),
        presenceOfElementLocated(By.id("key-val")),
        presenceOfElementLocated(By.className("issue-body-content"))
    )

    override fun countResults(): Int? = driver
        .findElements(By.className("showing"))
        .singleOrNull()
        ?.text
        ?.trim()
        ?.substringAfter("of ")
        ?.toInt()

    override fun listIssueKeys(): List<String> = JavaScriptUtils.executeScript(
        driver,
        "return Array.from(document.getElementsByClassName('issue-link-key'), i => i.innerText.trim())"
    )
}
