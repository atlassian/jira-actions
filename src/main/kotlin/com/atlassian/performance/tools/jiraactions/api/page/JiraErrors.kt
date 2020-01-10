package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.seleniumjs.NativeExpectedCondition
import com.atlassian.performance.seleniumjs.NativeExpectedConditions
import com.atlassian.performance.tools.jiraactions.api.webdriver.ExtraExpectedConditions
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
        return ExtraExpectedConditions.sometimesApply(or(*conditions))
    }

    fun anyCommonErrorNative(): NativeExpectedCondition {
        val conditions = errorLocators.map { NativeExpectedConditions.presenceOfElementLocated(it) }.toTypedArray()
        return NativeExpectedConditions.or(*conditions)
    }

    fun anyCommonWarning(): ExpectedCondition<WebElement> {
        return ExtraExpectedConditions.sometimesLocate(presenceOfElementLocated(warningMessageLocator))
    }

    fun assertNoErrors() {
        val detectErrors = NativeExpectedConditions.or(
            *errorLocators
                .map { NativeExpectedConditions.presenceOfElementLocated(it) }
                .toTypedArray()
        )
        if (!detectErrors.apply(driver)) {
            return
        }
        errorLocators.forEach { locator ->
            driver.findElements(locator).firstOrNull()?.let {  el ->
                val errorMessage = el.text
                throw Exception("Error at $el reads: $errorMessage")
            }
        }
    }
}
