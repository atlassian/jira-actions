package com.atlassian.performance.tools.jiraactions.page

import org.apache.logging.log4j.LogManager
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*

/**
 * Configures Rich Text Editor feature. Should work in any Jira version.
 * The admin access is complex, because RTE config was bugged before 7.3.0,
 * because it didn't enforce admin access on page visit.
 */
class RichTextEditorConfiguration(
    private val driver: WebDriver,
    private val access: AdminAccess
) {
    private val logger = LogManager.getLogger(this::class.java)
    private val switchLocator = By.id("rte-switch")

    /**
     * Makes sure RTE is disabled, even if the feature doesn't exist at all.
     * Supports both enabled and disabled websudo.
     */
    fun disable(): RichTextEditorConfiguration {
        val prompted = access.isPrompted()
        if (prompted) {
            access.gain()
        }
        if (driver.isElementPresent(switchLocator)) {
            ensureSwitchIsOff()
        } else {
            logger.info("This Jira does not support RTE configuration, so RTE should be de facto disabled")
        }
        if (prompted) {
            access.drop()
        }
        return this
    }

    /**
     * The switch always requires admin access, but it might not be granted yet.
     * For example Jira 7.2.0 did not enforce admin access on RTE config page.
     */
    private fun ensureSwitchIsOff() {
        if (getSwitchInput().isSelected) {
            logger.info("RTE is enabled, disabling...")
            if (access.isGranted()) {
                logger.info("Admin access already granted, clicking the RTE switch...")
                driver.wait(elementToBeClickable(switchLocator)).click()
                driver.wait(not(attributeToBe(getSwitchInput(), "aria-busy", "true")))
                logger.info("RTE should be disabled now")
            } else {
                logger.info("Admin access not granted yet, gaining access proactively and retrying...")
                access.gainProactively()
                ensureSwitchIsOff()
            }
        } else {
            logger.info("RTE is already disabled")
        }
    }

    private fun getSwitchInput() = driver.wait(elementToBeClickable(By.id("rte-switch-input")))
}