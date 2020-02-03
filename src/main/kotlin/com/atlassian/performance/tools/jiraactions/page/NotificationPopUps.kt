package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.wait
import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated
import java.time.Duration


internal class NotificationPopUps(private val driver: WebDriver) {
    private val auiFlagCloseLocator = By.cssSelector(".aui-flag .icon-close")

    fun dismissHealthCheckNotifications() : NotificationPopUps {
        // NPS is an AUI flag
        // e.g. healthcheck notifications: "Don't remind me again"
        return clickAll(By.cssSelector(".dismiss-notification"))
    }

    fun waitUntilAuiFlagsAreGone(): NotificationPopUps {
        try {
            waitUntilAuiFlagsAreGone(Duration.ofSeconds(10))
            return this
        } catch (e: Exception) {
            println("waitUntilAuiFlagsAreGone initial failure")
            e.printStackTrace()
            println((driver as RemoteWebDriver).getScreenshotAs(OutputType.BASE64))
        }

        try {
            dismissAuiFlags()
            waitUntilAuiFlagsAreGone(Duration.ofSeconds(30))
            return this
        } catch (e: Exception) {
            println("waitUntilAuiFlagsAreGone second failure")
            println((driver as RemoteWebDriver).getScreenshotAs(OutputType.BASE64))
            throw e
        }
    }

    private fun waitUntilAuiFlagsAreGone(duration: Duration) {
        driver.wait(
            duration, //this is animated and can take a looong time
            invisibilityOfElementLocated(By.id("aui-flag-container"))
        )
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
        println("disableNpsFeedback()")
        val clickAll = clickAll(By.id("nps-acknowledgement-accept-button"))
        println("/disableNpsFeedback()")
        return clickAll
    }

    fun dismissJiraHelpTips() : NotificationPopUps {
        return clickAll(By.cssSelector(".jira-help-tip .cancel"))
    }

    fun dismissPostSetup() : NotificationPopUps {
        return clickAll(By.className("postsetup-close-link"))
    }

    private fun clickAll(locator: By): NotificationPopUps {
        driver.findElements(locator)
//            .filter { it.isEnabled }
            .forEach {
                println("Clicking on: " + it.text)
                // this lets us click flags that are hidden behind other flags
                // otherwise we would have to wait for each flag to slide away
                JavaScriptUtils.executeScript<Any>(driver, "arguments[0].click()", it)
            }
        return this
    }
}
