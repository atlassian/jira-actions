package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.pagefactory.ByChained
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.time.Duration

internal class SingleSelect(
    private val driver: WebDriver,
    element: By
) {
    private val parent: By = ByChained(element, By.xpath(".."))
    private val input = ByChained(parent, By.tagName("input"))

    fun select(value: String) {
        val inputElement = driver.wait(
            timeout = Duration.ofSeconds(8),
            condition = elementToBeClickable(input)
        )
        inputElement.click()
        // this is a workaround for the fact that clear() can be processed asynchronously and can remove
        // some of the keys sent to the browser. We have tested that under stress load, sending one backspace
        // before the value fixes this issue.
        // Whether we lose the backspace or not, it doesn't matter for the overall result.
        inputElement.clear()
        inputElement.sendKeys(Keys.BACK_SPACE, value, Keys.ENTER)
    }

    fun getSuggestions(): List<String> {
        driver
            .wait(
                elementToBeClickable(
                    ByChained(parent, By.className("drop-menu"))
                )
            )
            .click()
        return driver
            .wait(
                presenceOfElementLocated(By.cssSelector(".ajs-layer.active"))
            )
            .findElements(By.className("aui-list-item-link"))
            .map { it.text }
    }

    fun getCurrentValue(): String {
        return driver.findElement(input).getAttribute("value")
    }
}
