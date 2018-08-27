package com.atlassian.performance.tools.jiraactions.page.form

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

internal class FormFieldFactory(
    private val driver: WebDriver,
    private val fieldDescriptors: List<FromFieldType>
) {
    fun getFormField(fieldGroup: WebElement): FormField {
        val matchedDescriptors = fieldDescriptors
            .filter { it.isTypeOf(getInput(fieldGroup)) }
            .toList()
        return when (matchedDescriptors.size) {
            1 -> matchedDescriptors[0].create(driver, fieldGroup, getInput(fieldGroup)!!)
            0 -> UnknownFormField(fieldGroup)
            else -> throw Exception("Field group '${fieldGroup.getAttribute("innerHTML")}' matches more than one form field" +
                "'$matchedDescriptors'")
        }
    }

    private fun getInput(fieldGroup: WebElement): WebElement? {
        val inputId = fieldGroup
            .findElements(By.tagName("label"))
            .mapNotNull { it.getAttribute("for") }
            .firstOrNull()

        return inputId?.let {
            fieldGroup
                .findElements(By.id(it))
                .singleOrNull()
        }
    }
}