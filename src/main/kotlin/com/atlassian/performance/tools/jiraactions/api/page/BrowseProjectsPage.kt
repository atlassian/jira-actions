package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.presenceOfElementLocated
import com.atlassian.performance.tools.jiraactions.api.memories.Project
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.time.Duration

class BrowseProjectsPage(
    private val driver: WebDriver
) {
    private val nextPageLocator = By.cssSelector(".aui-nav-next a")

    fun waitForProjectList(): BrowseProjectsPage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            Duration.ofSeconds(6),
            or(
                presenceOfElementLocated(By.cssSelector("tbody.projects-list")),
                presenceOfElementLocated(By.className("none-panel")),
                jiraErrors.anyCommonErrorNative()
            )
        )
        jiraErrors.assertNoErrors()
        return this
    }

    private fun hasNextPage(): Boolean {
        val element = getNextPageElement()
        return element != null && isNextPageEnabled(element)
    }

    private fun getNextPageElement(): WebElement? {
        val elements = driver.findElements(nextPageLocator)
        return when (elements.size) {
            1 -> elements.first()
            0 -> null
            else -> throw Exception("Unexpected state: BrowseProjects page contains more than 1 'Next page' elements")
        }
    }

    private fun isNextPageEnabled(element: WebElement): Boolean {
        return element
            .getAttribute("aria-disabled")
            ?.toBoolean()
            ?.not()
            ?: true
    }

    fun getNextPage(): Int? {
        if (!hasNextPage()) {
            return null
        }
        val projects = driver.findElement(nextPageLocator)
        val attribute = projects.getAttribute("href")
        return Regex("page=([0-9]+)").find(attribute)!!.groupValues.last().toInt()
    }

    fun getProjects(): Set<Project> {
        val projects = driver.findElements(By.cssSelector(".projects-list tr"))
        return projects.map {
            val projectName = it.findElement(By.cssSelector("td.cell-type-name a")).text.trim()
            val projectKey = it.findElement(By.cssSelector("td.cell-type-key, td.cell-type-name+td")).text
            Project(projectKey, projectName)
        }.toSet()
    }
}
