package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable

class TopNav(private val driver: WebDriver) {
    private val createButtonLocator = By.id("create_link")

    fun openIssueCreateDialog(): IssueCreateDialog {
        driver.wait(elementToBeClickable(createButtonLocator)).click()
        return IssueCreateDialog(driver)
    }

    fun isPresent(): Boolean {
        return driver.findElements(createButtonLocator).any()
    }
}
