package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class HistoryTabPanel(
    private val driver: WebDriver
) {
    private val loadMoreEntriesLocator = By.cssSelector("button.show-more-changehistory-tabpanel")

    fun loadAllHistoryEntries(): HistoryTabPanel {
        if (driver.isElementPresent(loadMoreEntriesLocator)) {
            val loadMoreButton = driver.findElement(loadMoreEntriesLocator)
            loadMoreButton.click()
            driver.wait(
                timeout = Duration.ofSeconds(60),
                condition = ExpectedConditions.stalenessOf(loadMoreButton)
            )
        } else {
            driver.wait(
                timeout = Duration.ofSeconds(60),
                condition = ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".issuePanelWrapper.loading"))
            )
        }
        return this
    }

    fun waitForActive(): HistoryTabPanel {
        driver.wait(
            Duration.ofSeconds(45),
            ExpectedConditions.elementToBeClickable(By.id("changehistory-tabpanel"))
        ).click()
        driver.wait(
            Duration.ofSeconds(60),
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("#changehistory-tabpanel.active-tab"))
        )
        return this
    }
}
