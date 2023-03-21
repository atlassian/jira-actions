package com.atlassian.performance.tools.jiraactions.administration

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

internal class InstanceHealthTab(
    private val driver: WebDriver
) {

    fun notifications(): InstanceHealthNotificationsConfig {
        val config = driver.wait(visibilityOfElementLocated(By.className("notification-config")))
        return InstanceHealthNotificationsConfig(config)
    }
}
