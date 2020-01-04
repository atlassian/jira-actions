package com.atlassian.performance.tools.jiraactions.webdriver

import com.atlassian.performance.tools.jiraactions.api.webdriver.JavaScriptUtils
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition

class NativeExpectedConditions {
    companion object {
        fun presenceOfElementLocated(locator: NativeBy): NativeExpectedCondition {
            return object : NativeExpectedCondition() {
                override fun render(): String {
                    return locator.render()
                }

                override fun toString(): String {
                    return "presence of element located by: ${render()}"
                }
            }
        }

        fun visibilityOfElementLocated(locator: NativeBy): NativeExpectedCondition {
            return object : NativeExpectedCondition() {
                override fun render(): String {
                    return "${getComputedStyle()}.display !== 'none' && " +
                        "parseFloat(${getComputedStyle()}.width)>0 && " +
                        "parseFloat(${getComputedStyle()}.height)>0"
                }

                private fun getComputedStyle() = "window.getComputedStyle(${locator.render()})"

                override fun toString(): String {
                    return "visibility of element located by: ${render()}"
                }
            }
        }

        fun and(vararg conditions: NativeExpectedCondition): NativeExpectedCondition {
            return object : CompositeNativeExpectedCondition(conditions, "&&") {
                override fun toString(): String {
                    return "all conditions need to be valid: " + render()
                }
            }
        }
        
        fun or(vararg conditions: NativeExpectedCondition): NativeExpectedCondition {
            return object : CompositeNativeExpectedCondition(conditions, "||") {
                override fun toString(): String {
                    return "at least one condition to be valid: " + render()
                }
            }
        }

        fun runOr(vararg conditions: NativeExpectedCondition): ExpectedCondition<Boolean> {
            return object : ExpectedCondition<Boolean> {
                override fun apply(driver: WebDriver?): Boolean {
                    return JavaScriptUtils.executeScript(driver!!, "return !!(${renderJs()})")
                }

                private fun renderJs() = or(*conditions).render()

                override fun toString(): String {
                    return "at least one condition to be valid: " + renderJs()
                }
            }
        }
    }
}

abstract class NativeExpectedCondition  {
    abstract fun render(): String
}

private open class CompositeNativeExpectedCondition(
    private val conditions: Array<out NativeExpectedCondition>, 
    private val operator: String
) : NativeExpectedCondition() {
    override fun render(): String {
        return conditions.joinToString(" $operator ") { "(" + it.render() + ")" }
    }
}
