package com.atlassian.performance.tools.jiraactions.page.form

import org.openqa.selenium.WebElement

class UnknownFormField(
    private val fieldGroup: WebElement
) : FormField {

    override fun hasValue(): Boolean {
        throw Exception("Can not read value from unknown form field - '${getHtml()}'")
    }

    override fun fillWithAnyValue() {
        throw Exception("Can not fill value for unknown form field - '${getHtml()}'")
    }

    private fun getHtml(): String {
        return this.fieldGroup.getAttribute("innerHTML")
    }
}