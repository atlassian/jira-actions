package com.atlassian.performance.tools.jiraactions.page.form

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import java.time.Instant.now

internal class TextSelectField(
    private val fieldGroup: WebElement,
    private val input: WebElement
) : FormField {
    private val random = SeededRandom(now().epochSecond)

    private val select: Select by lazy {
        Select(
            fieldGroup
                .findElements(By.tagName("select"))
                .single()
        )
    }

    override fun fillWithAnyValue() {
        val component = random.pick(select.options)!!.getAttribute("innerHTML").trim()
        input.sendKeys(component, Keys.RETURN)
    }

    override fun hasValue(): Boolean {
        return select
            .allSelectedOptions
            .isNotEmpty()
    }

    class Descriptor : FromFieldType {
        override fun isTypeOf(input: WebElement?): Boolean {
            return input != null && input.tagName == "textarea" && input.getAttribute("role") == "combobox"
        }

        override fun create(driver: WebDriver, fieldGroup: WebElement, input: WebElement): FormField {
            return TextSelectField(fieldGroup, input)
        }
    }
}
