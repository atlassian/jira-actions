package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.seleniumjs.NativeExpectedConditions
import com.atlassian.performance.seleniumjs.NativeExpectedConditions.Companion.or
import com.atlassian.performance.tools.jiraactions.api.memories.Project
import com.atlassian.performance.tools.jiraactions.page.UserProfilePage
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

class IssuePage(
    private val driver: WebDriver
) {
    private val reporterUserField = By.cssSelector("#reporter-val span")
    private val assigneeUserField = By.cssSelector("#assignee-val span")
    private val projectNameInBreadcrumbs = By.id("project-name-val")
    private val summaryField = By.id("summary-val")

    fun waitForSummary(): IssuePage {
        //needed to ensure ChromeDriver actually transitioned the page
        //this is a workaround. Remove the line to find out if the bug has been fixed
        driver.findElements(By.id("summary-val")).stream().findFirst()
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            condition = or(
                NativeExpectedConditions.visibilityOfElementLocated(summaryField),
                jiraErrors.anyCommonErrorNative()
            )
        )
        jiraErrors.assertNoErrors()
        return this
    }

    fun isEditable(): Boolean {
        return driver
            .findElements(By.id("edit-issue"))
            .firstOrNull()
            ?.getAttribute("href") != null
    }

    fun getReporter(): String? {
        return driver.findElements(reporterUserField).firstOrNull()?.getAttribute("rel")
    }

    fun getPossiblePriorities(): List<String>? {
        driver
            .findElements(By.id("priority-val"))
            .firstOrNull()
            ?.click()
            ?: return null
        return driver
            .wait(visibilityOfElementLocated(By.id("priority-suggestions")))
            .findElements(By.tagName("a"))
            .map { it.text }
    }

    fun getProject(): Project? {
        val projectNameElement = driver.findElement(projectNameInBreadcrumbs)
        val projectName = projectNameElement.text
        val projectKey = projectNameElement.getAttribute("href").split("/browse/").last()
        return Project(projectKey, projectName)
    }

    fun getAssignee(): String? {
        return driver.findElements(assigneeUserField).firstOrNull()?.getAttribute("rel")
    }

    internal fun visitReporterProfile(): UserProfilePage? {
        val reporter = getReporter() ?: return null
        return UserProfilePage(driver).navigateTo(reporter).waitForPageLoad()
    }

    fun getSummary(): String {
        return driver.findElement(By.id("summary-val")).text
    }

    fun getDescription(): String {
        return driver.findElement(By.cssSelector("#description-val .user-content-block")).text
    }

    @Deprecated("Do not use it. IssueId is not supported anymore")
    fun getIssueId(): Long = driver.findElement(By.id("key-val")).getAttribute("rel").toLong()
    fun getIssueType(): String = driver.findElement(By.id("type-val")).text

    fun openCommentTabPanel(): CommentTabPanel {
        return CommentTabPanel(driver).waitForActive()
    }

    fun openHistoryTabPanel(): HistoryTabPanel {
        return HistoryTabPanel(driver).waitForActive()
    }
}
