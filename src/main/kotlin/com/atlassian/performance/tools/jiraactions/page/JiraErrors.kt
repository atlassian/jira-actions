package com.atlassian.performance.tools.jiraactions.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated

class JiraErrors(
    private val driver: WebDriver
) {
    private val errorLocators = listOf(
        By.cssSelector("section div.aui-message.error"),
        By.id("errorPageContainer"),
        By.cssSelector("div.form-body div.error")
    )
    private val warningMessageLocator = By.cssSelector("section div.aui-message.warning")

    fun anyCommonError(): ExpectedCondition<Boolean> {
        val conditions = errorLocators.map { presenceOfElementLocated(it) }.toTypedArray()
        return or(*conditions)
    }

    fun anyCommonWarning(): ExpectedCondition<WebElement> {
        return presenceOfElementLocated(warningMessageLocator)
    }

    fun assertNoErrors() {
        errorLocators.forEach {
            if (driver.isElementPresent(it)) {
                val errorMessage = driver.findElement(it).text
                throw Exception("Error at $it reads: $errorMessage")
            }
        }
    }
}