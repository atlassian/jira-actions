package com.atlassian.jira.test.performance.actions.page.form

import com.atlassian.jira.test.performance.actions.SeededRandom
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.time.Instant.now

class Select(
    private val input: WebElement
) : FormField {
    private val random = SeededRandom(now().epochSecond)

    private val select: org.openqa.selenium.support.ui.Select by lazy {
        org.openqa.selenium.support.ui.Select(
            input
        )
    }

    override fun fillWithAnyValue() {
        select.selectByIndex(random.random.nextInt(select.options.size))
    }

    override fun hasValue(): Boolean {
        return select
            .allSelectedOptions
            .isNotEmpty()
    }

    class Descriptor : FromFieldType {
        override fun isTypeOf(input: WebElement?): Boolean {
            return input != null && input.tagName == "select"
        }

        override fun create(driver: WebDriver, fieldGroup: WebElement, input: WebElement): FormField {
            return Select(input)
        }
    }
}