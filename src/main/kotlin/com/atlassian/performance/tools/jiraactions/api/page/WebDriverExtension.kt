@file:JvmName("WebDriverUtils")

package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

/**
 * @return result of the condition
 */
@JvmOverloads
fun <T> WebDriver.wait(
    timeout: Duration,
    condition: ExpectedCondition<T>,
    precision: Duration = Duration.ofMillis(100)
): T {
    return WebDriverWait(
        this,
        timeout.seconds,
        precision.toMillis()
    ).until(condition)
}

/**
 * @return result of the condition
 */
internal fun <T> WebDriver.wait(
    condition: ExpectedCondition<T>
): T {
    return this.wait(Duration.ofSeconds(5), condition)
}

internal fun WebDriver.isElementPresent(
    locator: By
): Boolean {
    return this.findElements(locator).isNotEmpty()
}

internal fun WebDriver.maybeFindElement(
    locator: By
): WebElement? {
    return this.findElements(locator).singleOrNull()
}

/**
 * Makes sure the next navigation will not pop a dirty form alert even if a filled form was not submitted.
 * Use it just before filling in a form, because your action might fail at any moment.
 */
internal fun WebDriver.tolerateDirtyFormsOnCurrentPage() {
    (this as JavascriptExecutor).executeScript("window.onbeforeunload = null")
}
