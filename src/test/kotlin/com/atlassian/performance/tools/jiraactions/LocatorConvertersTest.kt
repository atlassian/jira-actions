package com.atlassian.performance.tools.jiraactions

import com.atlassian.performance.tools.jiraactions.webdriver.LocatorConverters
import org.junit.Test
import org.openqa.selenium.By

class LocatorConvertersTest {
    @Test
    fun byIdWorks(){ 
        LocatorConverters.toNativeBy(By.id("someId"))
    }

    @Test
    fun byClassNameWorks(){
        LocatorConverters.toNativeBy(By.className("someClass"))
    }

    @Test
    fun byCssSelectorWorks(){
        LocatorConverters.toNativeBy(By.cssSelector("someCssSelector"))
    }
}
