package com.atlassian.jira.test.performance.actions.page.form

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

interface FromFieldType {
    fun isTypeOf(input: WebElement?): Boolean
    fun create(driver: WebDriver, fieldGroup: WebElement, input: WebElement): FormField
}