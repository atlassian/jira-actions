package com.atlassian.performance.tools.jiraactions.administration

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

internal class SystemAdministrationPage(
    private val driver: WebDriver
) {

    /**
     * Element id, which works in 7.2.0.
     */
    private val supportToolsIdOld = "#support-tools-home"

    /**
     * Element id, which works in 7.13.0, 8.0.0 - 8.20.0 and 9.0.0.
     */
    private val supportToolsIdNew = "#troubleshooting-home"

    fun troubleshootingAndSupportTools(): TroubleShootingAndSupportToolsPage {
        driver.findElement(By.cssSelector("$supportToolsIdOld,$supportToolsIdNew")).click()
        return TroubleShootingAndSupportToolsPage(driver)
    }
}
