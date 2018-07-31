package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.memories.User
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
        loginForm.findElement(By.name("os_username")).sendKeys(user.name)
        loginForm.findElement(By.name("os_password")).sendKeys(user.password)
        loginForm.findElement(By.id("login-form-submit")).click()
        return DashboardPage(driver)
    }
}