package com.atlassian.performance.tools.jiraactions.administration

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select

internal class InstanceHealthNotificationsConfig(
    private val config: WebElement
) {
    fun dontShowAny() {
        Select(config.findElement(By.id("notification-settings")))
            .selectByValue("critical")
    }
}
