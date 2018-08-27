package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.api.WebJira
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class AdminAccess(
    private val driver: WebDriver,
    private val jira: WebJira,
    private val password: String
) {
    private val dropLocator = By.cssSelector(
        "#websudo-drop-from-protected-page, #websudo-drop-from-normal-page"
    )
    private val passwordLocator = By.id(
        "login-form-authenticatePassword"
    )

    fun isPrompted() = driver.isElementPresent(passwordLocator)

    fun isGranted(): Boolean {
        jira.goToSystemInfo()
        val granted = driver.isElementPresent(passwordLocator).not()
        driver.navigate().back()
        return granted
    }

    /**
     * Navigates to a known admin page in order to gain admin access.
     * Useful as a workaround for dysfunctional admin pages, which don't enforce the admin access on their own.
     */
    fun gainProactively() {
        jira.goToSystemInfo()
        gain()
        val navigation = driver.navigate()
        navigation.back()
        navigation.back()
    }

    fun gain() {
        driver.findElement(passwordLocator).sendKeys(password)
        driver.findElement(By.id("login-form-submit")).click()
    }

    fun drop() {
        jira.goToDashboard().waitForDashboard()
        if (isGranted()) {
            driver.findElement(dropLocator).click()
        }
    }
}