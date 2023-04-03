package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.util.*

fun splitTagTextIntoLines(text: String): List<String> {
    return text.replace("<br>", "\n", ignoreCase = true)
        .split("\n")
        .map { it.trim() }
        .filter { it.isNotBlank() }
}

internal class UserProfilePage(
    private val driver: WebDriver
) {
    private val headerUserInfo = By.id("header-details-user-fullname")
    private val headerProfileLink = By.id("view_profile")

    private val profileDetails = By.id("details-profile-fragment")

    private val userInformationDetails = By.cssSelector(".item-details dl")

    /**
     * Returns groups the user belongs to.
     * Warning! This will work only if the user used for testing has English locale.
     * @return list of groups the user belongs to.
     */
    fun getUserGroups(): List<String> {
        return (driver.findElements(userInformationDetails)
            .filter { it.findElement(By.tagName("dt")).text.trim().equals("Groups:") }
            .map { it.findElement(By.tagName("dd")) }
            .firstOrNull()
            ?.text
            ?.let { splitTagTextIntoLines(it) }
            ?: Collections.emptyList())
    }

    fun waitForPageLoad(): UserProfilePage {
        driver.wait(presenceOfElementLocated(profileDetails))
        return this
    }

    fun navigateTo(user: String?): UserProfilePage {
        driver.findElement(headerUserInfo).click()
        driver.wait(presenceOfElementLocated(headerProfileLink))
        val uri = driver.findElement(headerProfileLink).getAttribute("href")
        if (user == null) {
            driver.navigate().to(uri)
        } else {
            driver.navigate().to("$uri?name=$user")
        }
        return this
    }
}
