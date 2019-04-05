package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.JiraErrors
import com.atlassian.performance.tools.jiraactions.api.page.form.IssueForm
import com.atlassian.performance.tools.jiraactions.api.page.tolerateDirtyFormsOnCurrentPage
import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration
import java.util.function.Supplier

internal class IssueCreateDialog(
    private val driver: WebDriver
) {
    private val form = IssueForm(By.cssSelector("form[name=jiraform]"), driver)
    private val projectField = SingleSelect(driver, By.id("project-field"))
    private val issueTypeField = SingleSelect(driver, By.id("issuetype-field"))

    fun waitForDialog(): IssueCreateDialog {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            timeout = Duration.ofSeconds(30),
            condition = or(
                visibilityOfElementLocated(By.id("create-issue-dialog")),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        driver.tolerateDirtyFormsOnCurrentPage()
        return this
    }

    fun selectProject(projectName: String) = form.waitForRefresh(Supplier {
        projectField.select(projectName)
        return@Supplier this
    })

    fun selectIssueType(issueType: String) = form.waitForRefresh(Supplier {
        issueTypeField.select(issueType)
        return@Supplier this
    })

    fun getIssueTypes() = issueTypeField.getSuggestions()
        .plus(issueTypeField.getCurrentValue())

    fun fill(fieldId: String, value: String): IssueCreateDialog {
        driver.wait(elementToBeClickable(By.id(fieldId))).sendKeys(value)
        return this
    }

    fun fillRequiredFields(): IssueCreateDialog {
        form.fillRequiredFields()
        return this
    }

    fun submit() {
        driver.wait(elementToBeClickable(By.id("create-issue-submit"))).click()
        driver.wait(Duration.ofSeconds(30), invisibilityOfElementLocated(By.className("aui-blanket")))
    }
}