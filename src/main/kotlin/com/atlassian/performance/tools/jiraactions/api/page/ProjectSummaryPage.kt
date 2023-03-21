package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated

class ProjectSummaryPage(
    private val driver: WebDriver
) {

    fun waitForMetadata(): ProjectSummaryPage {
        driver.wait(presenceOfElementLocated(By.className("project-meta-column")))
        return this
    }
}
