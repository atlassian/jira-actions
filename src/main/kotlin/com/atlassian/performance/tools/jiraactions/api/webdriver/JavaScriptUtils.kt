package com.atlassian.performance.tools.jiraactions.api.webdriver

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver

class JavaScriptUtils {
    companion object {
        @JvmStatic
        fun <T> executeScript(driver: WebDriver, js: String, vararg args: Any): T {
            driver as JavascriptExecutor
            @Suppress("UNCHECKED_CAST")
            return driver.executeScript(js, *args) as T
        }
    }
}
