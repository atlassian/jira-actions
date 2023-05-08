package com.atlassian.performance.tools.jiraactions.page

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

internal fun WebElement.scrollIntoView(driver: WebDriver): WebElement {
    (driver as JavascriptExecutor).executeScript("arguments[0].scrollIntoView()", this)
    return this
}
