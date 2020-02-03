package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.pagefactory.ByChained
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.time.Duration

internal class SingleSelect(
    private val driver: WebDriver,
    locator: By
) {
    private val parent: By = ByChained(locator, By.xpath(".."))
    private val input = ByChained(parent, By.tagName("input"))

    fun select(value: String) {
        val inputElement = driver.wait(
            timeout = Duration.ofSeconds(8),
            condition = elementToBeClickable(input)
        )
        inputElement.click()
        inputElement.sendKeys(Keys.BACK_SPACE, value, Keys.TAB)
    }
    
    fun getSuggestions(): List<String> {
        val dropMenuArrow = driver
            .wait(
                elementToBeClickable(
                    ByChained(parent, By.className("drop-menu"))
                )
            )
        dropMenuArrow.click()
        val suggestions = driver
            .wait(
                presenceOfElementLocated(By.cssSelector(".ajs-layer.active"))
            )
            .findElements(By.className("aui-list-item-link"))
            .map { it.text }
        
        // we now have to restore the select to it's usual state,
        // otherwise we will mess up 'select-all-on-click' behaviour
        // We click to hide the menu and tab to get out of the input element
        dropMenuArrow.click()
        Actions(driver).sendKeys(Keys.TAB, Keys.TAB).build().perform()

        return suggestions
    }

    fun getCurrentValue(): String {
        return driver.findElement(input).getAttribute("value")
    }
}
