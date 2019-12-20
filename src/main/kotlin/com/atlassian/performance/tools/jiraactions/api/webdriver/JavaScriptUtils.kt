package com.atlassian.performance.tools.jiraactions.api.webdriver

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver

class JavaScriptUtils {
    companion object {

        @JvmStatic
        fun <T> executeScript(driver: WebDriver, js: String): T {
            driver as JavascriptExecutor
            return driver.executeScript(js) as T
        }
    }
}
