package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.wait
import com.atlassian.performance.tools.jiraactions.api.webdriver.sendKeysWhenClickable
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.pagefactory.ByChained
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated

internal class SingleSelect(
    private val driver: WebDriver,
    locator: By
) {
    private val parent: By = ByChained(locator, By.xpath(".."))
    private val inputLocator = ByChained(parent, By.tagName("input"))

    fun select(value: String) {
        driver.findElement(inputLocator).sendKeysWhenClickable(driver, Keys.BACK_SPACE, value, Keys.TAB)
    }

    fun select(picker: (List<String>) -> String) {
        val inputElement = driver.wait(elementToBeClickable(inputLocator))
        inputElement.click()

        val availableValues = parseSuggestions().plus(inputElement.getAttribute("value")!!)
        inputElement.sendKeys(Keys.BACK_SPACE, picker(availableValues), Keys.TAB)
    }

    fun getSuggestions(): List<String> {
        val dropMenuArrow = driver
            .wait(
                elementToBeClickable(
                    ByChained(parent, By.className("drop-menu"))
                )
            )
        dropMenuArrow.click()
        val suggestions = parseSuggestions()

        // we now have to restore the select to it's usual state,
        // otherwise we will mess up 'select-all-on-click' behaviour
        // We click to hide the menu and tab to get out of the input element
        dropMenuArrow.click()
        Actions(driver).sendKeys(Keys.TAB, Keys.TAB).build().perform()

        return suggestions
    }

    private fun parseSuggestions(): List<String> {
        return driver
                .wait(
                    presenceOfElementLocated(By.cssSelector(".ajs-layer.active"))
                )
                .findElements(By.className("aui-list-item-link"))
                .map { it.text }
    }

    fun getCurrentValue(): String {
        return driver.findElement(inputLocator).getAttribute("value")!!
    }
}
