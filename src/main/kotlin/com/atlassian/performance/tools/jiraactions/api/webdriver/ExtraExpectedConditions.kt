package com.atlassian.performance.tools.jiraactions.api.webdriver

import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import java.util.concurrent.atomic.AtomicInteger

class ExtraExpectedConditions {
    companion object {
        @JvmStatic
        fun sometimesApply(condition: ExpectedCondition<Boolean>): ExpectedCondition<Boolean> {
            return object : ExpectedCondition<Boolean> {
                val callCounter = AtomicInteger()

                override fun apply(input: WebDriver?): Boolean {
                    return callCounter.incrementAndGet() % 5 == 0 && condition.apply(input)!!
                }

                override fun toString(): String {
                    return "Sometimes: $condition"
                }
            }
        }

        @JvmStatic
        fun sometimesLocate(condition: ExpectedCondition<WebElement>): ExpectedCondition<WebElement> {
            return object : ExpectedCondition<WebElement> {
                val callCounter = AtomicInteger()

                override fun apply(input: WebDriver?): WebElement {
                    if (callCounter.incrementAndGet() % 5 != 0) {
                        throw NoSuchElementException("We haven't actually looked for $condition")
                    }
                    return condition.apply(input)!!
                }

                override fun toString(): String {
                    return "Sometimes: $condition"
                }
            }
        }
    }
}
