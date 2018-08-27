package com.atlassian.performance.tools.jiraactions.page.form

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable

internal class TextArea(
    private val driver: WebDriver,
    private val input: WebElement
) : FormField {

    override fun fillWithAnyValue() {
        driver.wait(
            elementToBeClickable(input)
        ).click()
        input.sendKeys("Lorem ipsum")
    }

    override fun hasValue(): Boolean {
        return input
            .getAttribute("value")
            .isNullOrBlank()
            .not()
    }

    class Descriptor : FromFieldType {
        override fun isTypeOf(input: WebElement?): Boolean {
            return input != null && input.getAttribute("role") == null && input.tagName == "textarea"
        }

        override fun create(driver: WebDriver, fieldGroup: WebElement, input: WebElement): FormField {
            return TextArea(driver, input)
        }
    }
}