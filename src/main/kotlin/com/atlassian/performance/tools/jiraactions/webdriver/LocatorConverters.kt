package com.atlassian.performance.tools.jiraactions.webdriver

import org.openqa.selenium.By

class LocatorConverters {
    companion object {
        fun toNativeBy(by: By) : NativeBy {
            return when (by) {
                is By.ById -> NativeBy.id(extractProperty(by, "id"))
                is By.ByClassName -> NativeBy.className(extractProperty(by, "className"))
                is By.ByCssSelector -> NativeBy.cssSelector(extractProperty(by, "selector"))
                else -> throw IllegalArgumentException("Don't know how to handle conversion of " + by + " " + by.javaClass)
            }
        }

        private fun extractProperty(obj: By, name: String) : String {
            val field = obj.javaClass.getDeclaredField(name)
            field.isAccessible = true
            return field.get(obj) as String
        }
    }
}
