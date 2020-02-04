package com.atlassian.performance.tools.jiraactions.page

import com.atlassian.performance.tools.jiraactions.api.page.JiraErrors
import com.atlassian.performance.tools.jiraactions.api.page.form.IssueForm
import com.atlassian.performance.tools.jiraactions.api.page.isElementPresent
import com.atlassian.performance.tools.jiraactions.api.page.tolerateDirtyFormsOnCurrentPage
import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedCondition
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
        waitUntilSummaryIsFocused()
        return@Supplier this
    })

    fun selectIssueType(issueType: String) = form.waitForRefresh(Supplier {
        issueTypeField.select(issueType)
        return@Supplier this
    })

    fun selectIssueType(picker: (Collection<String>) -> String) = form.waitForRefresh(Supplier {
        issueTypeField.select(picker)
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
        val configureFieldsDialogId = "inline-dialog-field_picker_popup"
        val dialogLocator = By.id(configureFieldsDialogId)
        try {
            openConfigureFieldsDialog(dialogLocator)
        } catch (e: TimeoutException) {
            //we probably sometimes click too fast, but no idea what we should wait for
            openConfigureFieldsDialog(dialogLocator)
        }
        val locator = By.xpath("//div[@id='$configureFieldsDialogId']//dd[1]//a")
        if (driver.isElementPresent(locator)) {
            driver.wait(elementToBeClickable(locator)).click()
            driver.wait(invisibilityOfElementLocated(dialogLocator))
            driver.wait(visibilityOfElementLocated(dialogLocator))
        }
        dismissConfigureFieldsDialog()
        return this
    }

    private fun openConfigureFieldsDialog(popupLocator: By) {
        val configureFields = driver.wait(elementToBeClickable(configColumnField))
        try {
            configureFields.click()
        } catch (e: ElementClickInterceptedException) {
            popUps
                .dismissHealthCheckNotifications() // nobody expects Spanish healthchecks!
                .waitUntilAuiFlagsAreGone()
            configureFields.click()
        }
        driver.wait(visibilityOfElementLocated(popupLocator))
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

    private fun waitUntilSummaryIsFocused() {
        driver.wait(Duration.ofSeconds(5), elementIsFocused(By.id("summary")))
    }

    private fun elementIsFocused(locator: By): ExpectedCondition<WebElement?> {
        return object : ExpectedCondition<WebElement?> {
            override fun apply(driver: WebDriver?): WebElement? {
                val summary = driver!!.findElement(locator)

                return if (summary == driver.switchTo().activeElement()) summary else null
            }

            override fun toString(): String {
                return "element to be focused: $locator"
            }
        }
    }

}
