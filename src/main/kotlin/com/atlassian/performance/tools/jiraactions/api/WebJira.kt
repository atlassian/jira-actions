package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.administration.JiraAdministrationMenu
import com.atlassian.performance.tools.jiraactions.api.page.*
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.net.URI
import java.net.URLEncoder

data class WebJira(
    val driver: WebDriver,
    val base: URI,
    private val adminPassword: String
) {

    fun goToLogin(): LoginPage {
        navigateTo("login.jsp")
        return LoginPage(driver)
    }

    fun configureRichTextEditor(): RichTextEditorConfiguration {
        navigateTo("secure/admin/ConfigureRTE!default.jspa")
        return RichTextEditorConfiguration(driver, accessAdmin())
    }

    fun configureBackupPolicy(): BackupConfiguration {
        navigateTo("secure/admin/ViewServices!default.jspa")
        return BackupConfiguration(driver, accessAdmin())
    }

    /**
     * Also known as WebSudo.
     */
    fun accessAdmin() = AdminAccess(
        driver = driver,
        jira = this,
        password = adminPassword
    )

    fun goToSystemInfo() {
        navigateTo("secure/admin/ViewSystemInfo.jspa")
    }

    fun goToIssue(
        issueKey: String
    ): IssuePage {
        navigateTo("browse/$issueKey")
        return IssuePage(driver)
    }

    fun getTopNav(): TopNav {
        return TopNav(driver)
    }

    fun goToDashboard(): DashboardPage {
        navigateTo("secure/Dashboard.jspa")
        return DashboardPage(driver)
    }

    fun goToProjectSummary(
        projectKey: String
    ): ProjectSummaryPage {
        navigateTo("browse/$projectKey/summary")
        return ProjectSummaryPage(driver)
    }

    fun goToIssueNavigator(
        jqlQuery: String
    ): IssueNavigatorPage {
        val encodedJqlQuery = URLEncoder.encode(jqlQuery, "UTF-8")
        navigateTo("issues/?jql=$encodedJqlQuery")
        return IssueNavigatorPage(driver, jqlQuery)
    }

    fun goToBrowseProjects(
        page: Int
    ): BrowseProjectsPage {
        navigateTo("secure/BrowseProjects.jspa?selectedCategory=all&selectedProjectType=all&page=$page")
        return BrowseProjectsPage(driver)
    }

    fun goToCommentForm(
        issueId: Long
    ): CommentForm {
        navigateTo("secure/AddComment!default.jspa?id=$issueId")
        return CommentForm(driver)
    }

    fun goToEditIssue(issueId: Long): EditIssuePage {
        navigateTo("secure/EditIssue!default.jspa?id=$issueId")
        return EditIssuePage(driver)
    }

    fun getJiraNode(): String {
        return driver.findElement(By.id("footer-build-information")).text
    }

    fun goToComment(
        commentUrl: String
    ): CommentTabPanel {
        driver.navigate().to(commentUrl)
        return CommentTabPanel(driver)
    }

    fun navigateTo(path: String) {
        driver.navigate().to(base.resolve(path).toURL())
    }

    internal fun administrate(): JiraAdministrationMenu {
        val menu = driver.findElement(By.id("admin_menu"))
        menu.click()
        return JiraAdministrationMenu(driver, menu)
    }
}
