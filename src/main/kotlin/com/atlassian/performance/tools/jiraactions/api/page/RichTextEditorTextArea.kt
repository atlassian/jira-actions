package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.*

/**
 * Represents the [Jira Rich Text Editor](https://confluence.atlassian.com/adminjiraserver/rich-text-editing-938847886.html)
 * falls back to the plain text editor if RTE is disabled.
 */
class RichTextEditorTextArea(
    private val driver: WebDriver,
    private val textArea: WebElement
) {

    fun overwriteIfPresent(text: String) {
        val attrClass = textArea.getAttribute("class")
        if(attrClass != null && attrClass.contains(other = "richeditor-cover", ignoreCase = true)){
            overwriteRich(text)
        }
        else{
            overwritePlain(text)
        }
    }

    private fun overwritePlain(text: String) {
        driver.wait(elementToBeClickable(textArea))
        textArea.clear()
        textArea.sendKeys(text)
    }

    private fun overwriteRich(text: String) {
        /**
         * the tinymce Iframe element structure:
         * div
         *   -- textarea
         *   -- div
         *     -- div
         *       -- div
         *         -- div
         *           --iframe
         */
        val iframeXpath = "//div[textarea[@id='${textArea.getAttribute("id")}']]//iframe"

        try {
            driver.wait(frameToBeAvailableAndSwitchToIt(By.xpath(iframeXpath)))
            val tinyMce = driver.findElement(By.id("tinymce"))
            driver.wait(attributeToBeNotEmpty(tinyMce, "data-projectkey"))
            tinyMce.clear()
            tinyMce.sendKeys(text)
        } finally {
            driver.switchTo().parentFrame()
        }
    }

}
