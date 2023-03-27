package com.atlassian.performance.tools.jiraactions.administration

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

internal class SystemAdministrationPage(
    private val driver: WebDriver
) {
    fun troubleshootingAndSupportTools(): TroubleShootingAndSupportToolsPage {
        driver.findElement(By.id("troubleshooting-home")).click()
        return TroubleShootingAndSupportToolsPage(driver)
    }
}
