package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.page.IssueCreateDialog
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.and
import java.time.Duration

class DashboardPage(
    private val driver: WebDriver
) {
    private val jiraErrors = JiraErrors(driver)

    fun dismissAllPopups() {
        Thread.sleep(4000)
        driver.findElements(By.cssSelector(".aui-flag .icon-close")).filter { it.isEnabled && it.isDisplayed }.forEach { it.click() }
        driver.findElements(By.id("nps-acknowledgement-accept-button")).filter { it.isEnabled && it.isDisplayed }.forEach { it.click() }
        driver.findElements(By.cssSelector(".jira-help-tip .cancel")).filter { it.isEnabled && it.isDisplayed }.forEach { it.click() }
        driver.findElements(By.className("postsetup-close-link")).filter { it.isEnabled && it.isDisplayed }.forEach { it.click() }
        driver.findElements(By.className("helptip-close")).filter { it.isEnabled && it.isDisplayed }.forEach { it.click() }
        Thread.sleep(4000)
    }

    fun waitForDashboard(): DashboardPage {
        val gadgets = driver.findElements(By.cssSelector("#dashboard-content > .gadget"))
        waitForIframes(gadgets)
        jiraErrors.assertNoErrors()
        return this
    }

    private fun waitForIframes(
        gadgets: List<WebElement>
    ): DashboardPage {
        val iframes = gadgets.flatMap { it.findElements(By.tagName("iframe")) }
        val iframesHaveContent = iframes.map { iframe ->
            ExpectedCondition {
                driver.switchTo().frame(iframe)
                val iframeContent = driver.findElement(By.tagName("body")).text
                driver.switchTo().parentFrame()
                return@ExpectedCondition iframeContent.isNotBlank()
            }
        }
        driver.wait(
            condition = and(*iframesHaveContent.toTypedArray()),
            timeout = Duration.ofSeconds(20)
        )
        return this
    }

    internal fun openIssueCreateDialog(): IssueCreateDialog {
        driver.findElement(By.id("create_link")).click()
        return IssueCreateDialog(driver)
    }
}
