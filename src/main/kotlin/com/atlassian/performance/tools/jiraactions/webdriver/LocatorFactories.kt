package com.atlassian.performance.tools.jiraactions.webdriver

import org.openqa.selenium.By

class LocatorFactories {
    companion object {
        fun cssSelector(selector: String): LocatorFactory {
            return CssSelectorLocatorFactory(selector)
        }

        fun id(id: String): LocatorFactory {
            return ByIdLocatorFactory(id)
        }
    }

    abstract class LocatorFactory {
        abstract fun seleniumLocator(): By
        abstract fun nativeLocator(): NativeBy
    }

    class CssSelectorLocatorFactory(private val selector: String) : LocatorFactory() {
        override fun seleniumLocator(): By {
            return By.cssSelector(selector)
        }

        override fun nativeLocator(): NativeBy {
            return NativeBy.cssSelector(selector)
        }
    }

    class ByIdLocatorFactory(private val id: String) : LocatorFactory() {
        override fun seleniumLocator(): By {
            return By.id(id)
        }

        override fun nativeLocator(): NativeBy {
            return NativeBy.id(id)
        }
    }
}
