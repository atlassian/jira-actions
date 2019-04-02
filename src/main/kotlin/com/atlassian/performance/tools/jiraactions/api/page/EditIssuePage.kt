package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.page.form.IssueForm
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions.*
import org.openqa.selenium.support.ui.Select
import java.time.Duration


class EditIssuePage(
    private val driver: WebDriver
) {
    private val form = IssueForm(By.id("issue-edit"), driver)
    private val summaryLocator = By.id("summary")
    private val updateButtonLocator = By.id("issue-edit-submit")

    fun waitForEditIssueForm(): EditIssuePage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            timeout = Duration.ofSeconds(5),
            condition = or(
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
        val descriptionFieldId = "description"
        if(driver.isElementPresent(By.id(descriptionFieldId))){
            RichTextEditorTextArea(driver, driver.findElement(By.id(descriptionFieldId)))
                .overwriteIfPresent("description")
        }
        form.fillRequiredFields()
        selectResolution()
        return this
    }

    private fun selectResolution() {
        val resolutionLocator = By.id("resolution")
        if (driver.isElementPresent(resolutionLocator)) {
            val dropDown = Select(driver.findElement(resolutionLocator))
            if(dropDown.options != null && dropDown.options.size > 1){
                val selection = dropDown.options.get(1)
                dropDown.selectByVisibleText(selection.text)
            }
        }
    }

    fun submit(): IssuePage {
        driver.findElement(updateButtonLocator).click()
        return IssuePage(driver).waitForSummary()
    }

    private fun By.clearAndTypeIfPresent(text: String) {
        if (driver.isElementPresent(this)) {
            val webElement = driver.findElement(this)
            driver.wait(timeout = Duration.ofSeconds(2), condition = elementToBeClickable(webElement))
            webElement.clear()
            webElement.sendKeys(text)
        }
    }

}
