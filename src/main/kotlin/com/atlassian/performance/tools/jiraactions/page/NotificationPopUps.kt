package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.wait
import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.not
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated


internal class NotificationPopUps(private val driver: WebDriver) {
    private val auiFlagCloseLocator = By.cssSelector(".aui-flag .icon-close")
    
    fun dismissHealthCheckNotifications() : NotificationPopUps {
        // NPS is an AUI flag
        // e.g. healthcheck notifications: "Don't remind me again"
        return clickAll(By.cssSelector(".dismiss-notification"))
    }
    
    fun waitUntilAuiFlagsAreGone(): NotificationPopUps {
        try {
            driver.wait(
                not(presenceOfElementLocated(auiFlagCloseLocator))
            )
        } catch (e: Exception) {
            println((driver as RemoteWebDriver).getScreenshotAs(OutputType.BASE64))
            throw e
        }
        return this
    }

    fun dismissAuiFlags() : NotificationPopUps {
        // X mark on AUI flag
        return clickAll(auiFlagCloseLocator)
    }

    fun dismissFindYourWorkFaster() : NotificationPopUps {
        // Find your work faster "OK, got it"
        return clickAll(By.cssSelector(".aui-button.helptip-close"))
    }

    fun disableNpsFeedback() : NotificationPopUps {
        //NPS is an AUI flag
        return clickAll(By.id("nps-acknowledgement-accept-button"))
    }

    fun dismissJiraHelpTips() : NotificationPopUps {
        return clickAll(By.cssSelector(".jira-help-tip .cancel"))
    }

    fun dismissPostSetup() : NotificationPopUps {
        return clickAll(By.className("postsetup-close-link"))
    }

    private fun clickAll(locator: By): NotificationPopUps {
        driver.findElements(locator)
            .filter { it.isEnabled && it.isDisplayed }
            .forEach {
                //this lets us click flags that are hidden behind other flags
                //otherwise we would have to wait for each flag
                JavaScriptUtils.executeScript<Any>(driver, "arguments[0].click()", it)
            }
        return this
    }
}
