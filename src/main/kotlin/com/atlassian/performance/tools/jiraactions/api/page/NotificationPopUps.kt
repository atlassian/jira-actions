package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated


class NotificationPopUps(private val driver: WebDriver) {
    private val auiFlagCloseLocator = By.cssSelector(".aui-flag .icon-close, .aui-flag .aui-close-button")

    fun dismissHealthCheckNotifications(): NotificationPopUps {
        // NPS is an AUI flag
        // e.g. healthcheck notifications: "Don't remind me again"
        return clickAll(By.cssSelector(".dismiss-notification"))
    }

    fun waitUntilAuiFlagsAreGone(): NotificationPopUps {
        try {
            waitUntilAuiFlagsAreInvisible()
            return this
        } catch (e: TimeoutException) {
        }

        try {
            dismissAuiFlags()
            waitUntilAuiFlagsAreInvisible()
            return this
        } catch (e: Exception) {
            throw e
        }
    }

    private fun waitUntilAuiFlagsAreInvisible() {
        driver.wait(invisibilityOfElementLocated(By.id("aui-flag-container")))
    }

    fun dismissAuiFlags(): NotificationPopUps {
        // X mark on AUI flag
        return clickAll(auiFlagCloseLocator)
    }

    fun dismissFindYourWorkFaster(): NotificationPopUps {
        // Find your work faster "OK, got it"
        return clickAll(By.cssSelector(".aui-button.helptip-close"))
    }

    fun disableNpsFeedback(): NotificationPopUps {
        //NPS is an AUI flag
        return clickAll(By.id("nps-acknowledgement-accept-button"))
    }

    fun dismissJiraHelpTips(): NotificationPopUps {
        return clickAll(By.cssSelector(".jira-help-tip .cancel"))
    }

    fun dismissPostSetup(): NotificationPopUps {
        return clickAll(By.className("postsetup-close-link"))
    }

    private fun clickAll(locator: By): NotificationPopUps {
        driver.findElements(locator)
            .forEach {
                // this lets us click flags that are hidden behind other flags
                // otherwise we would have to wait for each flag to slide away
                JavaScriptUtils.executeScript<Any>(driver, "arguments[0].click()", it)
            }
        return this
    }
}
