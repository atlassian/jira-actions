package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.memories.Project
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class BrowseProjectsPage(
    private val driver: WebDriver
) {
    private val nextPageLocator = By.cssSelector(".aui-nav-next a")

    fun waitForProjectList(): BrowseProjectsPage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            Duration.ofSeconds(6),
            ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("tbody.projects-list")),
                ExpectedConditions.presenceOfElementLocated(By.className("none-panel")),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        return this
    }

    private fun hasNextPage(): Boolean {
        return !isNextPageDisabled()
    }

    private fun isNextPageDisabled(): Boolean {
        val nextPage = driver.findElement(nextPageLocator)
        return nextPage.getAttribute("aria-disabled")
            ?.toBoolean()
            ?: false
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