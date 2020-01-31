package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.seleniumjs.LocatorConverters
import com.atlassian.performance.tools.jiraactions.api.page.wait
import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.pagefactory.ByChained
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.time.Duration

internal class SingleSelect(
    private val driver: WebDriver,
    private val locator: By
) {
    private val parent: By = ByChained(locator, By.xpath(".."))
    private val input = ByChained(parent, By.tagName("input"))

    fun select(value: String) {
        val inputElement = driver.wait(
            timeout = Duration.ofSeconds(8),
            condition = elementToBeClickable(input)
        )
        //unfortunately, there are race conditions in the single select, so 'just typing' won't work
        //there are also race conditions around .clear()
        //we have to manually remove the content of the field with backspaces
        inputElement.click()
        val valueLength = getLength(locator)
        inputElement.sendKeys(Keys.BACK_SPACE.repeat(valueLength), value, Keys.ENTER)
    }

    private fun getLength(locator: By): Int {
        //note: inputElement.text will return empty text at this point, we need to use JavaScript :-(
        val nativeLocator = try {
            LocatorConverters.toNativeBy(locator)
        } catch (e: Exception) {
            // conversion failed, let's hope that sending one backspace will be enough.
            return 1
        }
        val inputElementText = JavaScriptUtils.executeScript<String>(driver, "return " + nativeLocator.render() + ".value")
        return inputElementText.length
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
