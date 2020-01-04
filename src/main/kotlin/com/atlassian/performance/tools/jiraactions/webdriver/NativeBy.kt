package com.atlassian.performance.tools.jiraactions.webdriver

abstract class NativeBy(private val js: String) {
    fun render(): String {
        return js
    }

    companion object {
        fun id(id: String): NativeBy {
            return NativeById(id)
        }

        fun cssSelector(selector: String): NativeBy {
            return NativeByCssSelector(selector)
        }

        fun className(className: String): NativeBy {
            return NativeByClassName(className)
        }
    }
}

private fun escape(selector: String) = selector.replace("'", "\'")

private class NativeById(id: String) : NativeBy("document.getElementById('$id')")
private class NativeByCssSelector(cssSelector: String) : NativeBy("document.querySelector('${escape(cssSelector)}')")
private class NativeByClassName(className: String) : NativeBy("document.getElementsByClassName('$className')[0]")
