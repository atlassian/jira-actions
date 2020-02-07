package com.atlassian.performance.tools.jiraactions.api.webdriver

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions

fun WebElement.sendKeysWhenClickable(driver: WebDriver, vararg keysToSend: CharSequence): WebElement {
    driver.wait(ExpectedConditions.elementToBeClickable(this))
    click()

    sendKeys(*keysToSend)
    return this
}

fun WebElement.sendKeysAndValidate(driver: WebDriver, text: String): WebElement {
    sendKeysWhenClickable(driver, text as CharSequence)

    // It's hard to say when the keys can be sent. They seem to randomly get lost.
    for (i in 0..10) {
        val actualValue = getAttribute("value") ?: ""
        if (actualValue == text) {
            break
        }
        Thread.sleep(100)
        sendKeys(Keys.BACK_SPACE.repeat(actualValue.length), text)
    }
    return this
}

