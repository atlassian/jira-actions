package com.atlassian.performance.tools.jiraactions.api.page

import org.apache.logging.log4j.LogManager
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class BackupConfiguration(
    private val driver: WebDriver,
    private val access: AdminAccess
) {
    private val logger = LogManager.getLogger(this::class.java)
    private val deleteBackupLocator = By.xpath(
        "//*[@id='tbl_services']" +
            "//*[contains(text(), 'com.atlassian.jira.service.services.export.ExportService')]" +
            "/ancestor::tr" +
            "//*[contains(@class, 'operations-list')]" +
            "//*[contains(text(), 'Delete')]"
    )

    fun delete(): BackupConfiguration {
        val prompted = access.isPrompted()
        if (prompted) {
            access.gain()
        }
        while (driver.isElementPresent(deleteBackupLocator)) {
            deleteBackupService()
        }
        logger.info("Backup services have been deleted")
        if (prompted) {
            access.drop()
        }
        return this
    }

    private fun deleteBackupService() {
        driver.wait(ExpectedConditions.elementToBeClickable(deleteBackupLocator)).click()
        driver.wait(ExpectedConditions.alertIsPresent())
        driver.switchTo().alert().accept()
        if (access.isPrompted()) {
            access.gain()
        }
    }
}
