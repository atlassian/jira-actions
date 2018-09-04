package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.page.form.IssueForm
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

class EditIssuePage(
    private val driver: WebDriver
) {
    private val form = IssueForm(By.id("issue-edit"), driver)
    private val summaryLocator = By.id("summary")
    private val descriptionLocator = By.id("description")
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
        descriptionLocator.clearAndTypeIfPresent("description")
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
            driver.wait(timeout = Duration.ofSeconds(2), condition = elementToBeClickable(webElement))
            webElement.clear()
            webElement.sendKeys(text)
        }
    }
}