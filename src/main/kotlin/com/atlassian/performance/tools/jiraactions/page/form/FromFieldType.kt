package com.atlassian.performance.tools.jiraactions.page.form

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

internal interface FromFieldType {
    fun isTypeOf(input: WebElement?): Boolean
    fun create(driver: WebDriver, fieldGroup: WebElement, input: WebElement): FormField
}