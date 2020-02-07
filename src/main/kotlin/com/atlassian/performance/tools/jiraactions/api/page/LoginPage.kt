package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.webdriver.sendKeysAndValidate
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class LoginPage(
    private val driver: WebDriver
) {
    private val loginFormLocator = By.id("login-form")

    fun logIn(
        user: User
    ): DashboardPage {
        driver.wait(
            Duration.ofMinutes(4),
            ExpectedConditions.presenceOfElementLocated(loginFormLocator)
        )
        val loginForm = driver.findElement(loginFormLocator)
        loginForm.findElement(By.name("os_username")).sendKeysAndValidate(driver, user.name)
        loginForm.findElement(By.name("os_password")).sendKeysAndValidate(driver, user.password)
        loginForm.findElement(By.id("login-form-submit")).click()
        return DashboardPage(driver)
    }
}
