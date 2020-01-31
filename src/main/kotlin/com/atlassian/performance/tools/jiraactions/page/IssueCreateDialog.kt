package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.JiraErrors
import com.atlassian.performance.tools.jiraactions.api.page.form.IssueForm
import com.atlassian.performance.tools.jiraactions.api.page.isElementPresent
import com.atlassian.performance.tools.jiraactions.api.page.tolerateDirtyFormsOnCurrentPage
import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.ElementClickInterceptedException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration
import java.util.function.Supplier

internal class IssueCreateDialog(
    private val driver: WebDriver
) {
    private val popUps = NotificationPopUps(driver)

    private val form = IssueForm(By.cssSelector("form[name=jiraform]"), driver)
    private val projectField = SingleSelect(driver, By.id("project-field"))
    private val issueTypeField = SingleSelect(driver, By.id("issuetype-field"))
    private val configColumnField = By.id("qf-field-picker-trigger")

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

    /**
     * Click 'Configure Fields' to display 'All' to ensure
     * all mandatory fields are displayed in creation dialog.
     *
     */
    fun showAllFields(): IssueCreateDialog {
        openConfigureFieldsDialog()
        val configureFieldsDialogId = "inline-dialog-field_picker_popup"
        driver.wait(visibilityOfElementLocated(By.id(configureFieldsDialogId)))
        val locator = By.xpath("//div[@id='$configureFieldsDialogId']//dd[1]//a")
        if (driver.isElementPresent(locator)) {
            driver.wait(elementToBeClickable(locator)).click()
            driver.wait(invisibilityOfElementLocated(By.id(configureFieldsDialogId)))
            driver.wait(visibilityOfElementLocated(By.id(configureFieldsDialogId)))
        }
        dismissConfigureFieldsDialog()
        return this
    }

    private fun openConfigureFieldsDialog() {
        val configureFields = driver.wait(elementToBeClickable(configColumnField))
        try {
            configureFields.click()
        } catch (e: ElementClickInterceptedException) {
            popUps.dismissHealthCheckNotifications() // nobody expects Spanish healthchecks!
            configureFields.click()
        }
    }

    private fun dismissConfigureFieldsDialog(){
        driver.wait(elementToBeClickable(By.xpath("//div[@id='create-issue-dialog']//h2"))).click()
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
