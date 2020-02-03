package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.Patience
import com.atlassian.performance.tools.jiraactions.api.WebJira
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable

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

    fun isPrompted(): Boolean {
        return Patience().test {
            driver.isElementPresent(passwordLocator)
        }
    }

    fun isGranted(): Boolean {
        jira.goToSystemInfo()
        val granted = isPrompted().not()
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
        driver.wait(elementToBeClickable(passwordLocator)).sendKeys(password)
        driver.findElement(By.id("login-form-submit")).click()
    }

    fun drop() {
        jira.goToDashboard().waitForDashboard()
        if (isGranted()) {
            driver.wait(elementToBeClickable(dropLocator)).click()
        }
    }
}