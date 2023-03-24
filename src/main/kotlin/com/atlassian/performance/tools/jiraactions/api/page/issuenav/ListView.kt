package com.atlassian.performance.tools.jiraactions.api.page.issuenav

import com.atlassian.performance.seleniumjs.NativeExpectedCondition
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.and
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.presenceOfElementLocated
import com.atlassian.performance.tools.jiraactions.api.page.isElementPresent
import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import java.time.Duration

class ListView(
    private val driver: WebDriver
) : IssueNavResultsView {

    override fun isSelected(): Boolean {
        driver.wait(Duration.ofSeconds(10), presenceOfElementLocated(By.className("results-panel")))
        return driver.isElementPresent(By.className("list-view"))
    }

    override fun switchToView() {
        driver.wait(elementToBeClickable(By.id("layout-switcher-button"))).click()
        driver.wait(elementToBeClickable(By.cssSelector("[data-layout-key=list-view]"))).click()
    }

    override fun detectResults(): NativeExpectedCondition = and(
        presenceOfElementLocated(By.id("issuetable")),
        presenceOfElementLocated(By.className("issuerow"))
    )

    override fun countResults(): Int? = driver
        .findElements(By.className("results-count-total"))
        .singleOrNull()
        ?.text
        ?.trim()
        ?.toInt()

    override fun listIssueKeys(): List<String> = driver
        .findElements(By.cssSelector("[data-issuekey]"))
        .map { it.getAttribute("[data-issuekey") }
}
