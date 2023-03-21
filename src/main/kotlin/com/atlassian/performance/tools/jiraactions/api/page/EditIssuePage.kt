package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.api.page.form.IssueForm
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*

class EditIssuePage(
    private val driver: WebDriver
) {
    private val form = IssueForm(By.id("issue-edit"), driver)
    private val summaryLocator = By.id("summary")
    private val updateButtonLocator = By.id("issue-edit-submit")
    private val descriptionFieldLocator = By.id("description")

    fun waitForEditIssueForm(): EditIssuePage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            or(
                presenceOfElementLocated(updateButtonLocator),
                jiraErrors.anyCommonError(),
                jiraErrors.anyCommonWarning()
            )
        )
        jiraErrors.assertNoErrors()
        driver.tolerateDirtyFormsOnCurrentPage()
        return this
    }

    fun fillForm(): EditIssuePage {
        summaryLocator.clearAndTypeIfPresent("summary")
        if (driver.isElementPresent(descriptionFieldLocator)) {
            RichTextEditorTextArea(driver, driver.findElement(descriptionFieldLocator))
                .overwriteIfPresent("description")
        }
        form.fillRequiredFields()
        return this
    }

    fun submit(): IssuePage {
        driver.findElement(updateButtonLocator).click()
        return IssuePage(driver).waitForSummary()
    }

    private fun By.clearAndTypeIfPresent(text: String) {
        if (driver.isElementPresent(this)) {
            val webElement = driver.findElement(this)
            driver.wait(elementToBeClickable(webElement))
            webElement.clear()
            webElement.sendKeys(text)
        }
    }
}
