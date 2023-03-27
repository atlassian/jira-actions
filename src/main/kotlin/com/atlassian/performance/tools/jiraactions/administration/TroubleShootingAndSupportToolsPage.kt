package com.atlassian.performance.tools.jiraactions.administration

import org.openqa.selenium.WebDriver

internal class TroubleShootingAndSupportToolsPage(
    private val driver: WebDriver
) {
    fun instanceHealth(): InstanceHealthTab {
        return InstanceHealthTab(driver)
    }
}
