package com.atlassian.performance.tools.jiraactions.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

internal class NotificationPopUps(private val driver: WebDriver) {
    fun dismissHealthCheckNotifications() : NotificationPopUps {
        // e.g. healthcheck notifications: "Don't remind me again"
        return clickAll(By.cssSelector(".dismiss-notification"))
    }

    fun dismissAuiFlags() : NotificationPopUps {
        // X mark on AUI flag
        return clickAll(By.cssSelector(".aui-flag .icon-close"))
    }

    fun dismissFindYourWorkFaster() : NotificationPopUps {
        // Find your work faster "OK, got it"
        return clickAll(By.cssSelector(".aui-button.helptip-close"))
    }

    fun disableNpsFeedback() : NotificationPopUps {
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
            .forEach { it.click() }
        return this
    }
}
