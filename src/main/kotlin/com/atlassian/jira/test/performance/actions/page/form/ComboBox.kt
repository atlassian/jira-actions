package com.atlassian.jira.test.performance.actions.page.form

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class ComboBox : FormField {

    override fun fillWithAnyValue() {
        throw NotImplementedError()
    }

    override fun hasValue(): Boolean {
        return true
    }

    class Descriptor : FromFieldType {
        override fun isTypeOf(input: WebElement?): Boolean {
            return input != null && input.tagName == "input" && input.getAttribute("role") == "combobox"
        }

        override fun create(driver: WebDriver, fieldGroup: WebElement, input: WebElement): FormField {
            return ComboBox()
        }
    }
}