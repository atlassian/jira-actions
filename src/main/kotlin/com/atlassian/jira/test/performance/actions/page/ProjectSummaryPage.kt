package com.atlassian.jira.test.performance.actions.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class ProjectSummaryPage(
    private val driver: WebDriver
) {

    fun waitForMetadata(): ProjectSummaryPage {
        driver.wait(
            Duration.ofSeconds(6),
            ExpectedConditions.presenceOfElementLocated(By.className("project-meta-column"))
        )
        return this
    }
}