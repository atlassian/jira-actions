package com.atlassian.performance.tools.jiraactions.administration

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

internal class JiraAdministrationMenu(
    private val driver: WebDriver,
    private val menu: WebElement
) {
    fun system(): SystemAdministrationPage {
        menu.findElement(By.id("admin_system_menu")).click()
        return SystemAdministrationPage(driver)
    }
}
