package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.JiraNodeIdMemory
import com.atlassian.performance.tools.jiraactions.administration.JiraAdministrationMenu
import com.atlassian.performance.tools.jiraactions.api.page.*
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.net.URI
import java.net.URLEncoder

data class WebJira(
    val driver: WebDriver,
    val base: URI,
    val adminPassword: String
) {
    private val jiraNodeMemory = JiraNodeIdMemory()

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
        return jiraNodeMemory.recallNodeId()
            ?: (findNodeId()
                .takeUnless { it.isNullOrBlank() }
                ?.also { jiraNodeMemory.rememberNodeId(it) })
            ?: throw Exception("Could not find Jira node ID")
    }

    /**
     * Based on [How to determine which node a user is accessing in Datacenter](https://confluence.atlassian.com/jirakb/how-to-determine-which-node-a-user-is-accessing-in-datacenter-885252555.html)
     */
    private fun findNodeId(): String? {
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
        NotificationPopUps(driver).waitUntilAuiFlagsAreGone()
        driver.findElement(By.id("admin_menu")).click()
        val menu = driver.findElement(By.id("system-admin-menu-content"))
        return JiraAdministrationMenu(driver, menu)
    }
}
