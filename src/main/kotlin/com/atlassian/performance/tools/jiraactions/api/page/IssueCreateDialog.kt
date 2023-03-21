package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.api.page.form.IssueForm
import com.atlassian.performance.tools.jiraactions.api.webdriver.sendKeysWhenClickable
import com.atlassian.performance.tools.jiraactions.page.SingleSelect
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.*
import org.openqa.selenium.support.ui.Select
import java.util.function.Supplier

class IssueCreateDialog(
    private val driver: WebDriver
) {
    private val popUps = NotificationPopUps(driver)

    private val form = IssueForm(By.cssSelector("form[name=jiraform]"), driver)
    private val projectField = SingleSelect(driver, By.id("project-field"))
    private val issueTypeField = SingleSelect(driver, By.id("issuetype-field"))
    private val configColumnField = By.id("qf-field-picker-trigger")
    private val dialog = By.id("create-issue-dialog")

    fun waitForDialog(): IssueCreateDialog {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            or(
                visibilityOfElementLocated(dialog),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        driver.tolerateDirtyFormsOnCurrentPage()
        return this
    }

    private fun waitForDialogToHide() {
        driver.wait(invisibilityOfElementLocated(dialog)) }

    fun selectProject(projectName: String) = form.waitForRefresh(Supplier {
        projectField.select(projectName)
        waitUntilSummaryIsFocused()
        return@Supplier this
    })

    fun selectIssueType(issueType: String) = form.waitForRefresh(Supplier {
        issueTypeField.select(issueType)
        return@Supplier this
    })

    fun selectIssueType(picker: (List<String>) -> String) = form.waitForRefresh(Supplier {
        issueTypeField.select(picker)
        return@Supplier this
    })

    fun getIssueTypes() = issueTypeField.getSuggestions()
        .plus(issueTypeField.getCurrentValue())

    fun fill(fieldId: String, value: String): IssueCreateDialog {
        driver.findElement(By.id(fieldId)).sendKeysWhenClickable(driver, value)
        return this
    }

    /**
     * Click 'Configure Fields' to display 'All' to ensure
     * all mandatory fields are displayed in creation dialog.
     *
     * The view has changed since Jira 8.19 so differentiator is needed
     */
    fun showAllFields(): IssueCreateDialog {
        val configureFieldsDialogClass = "qf-picker"
        val dialogLocator = By.className(configureFieldsDialogClass)
        val selectLocator = getFieldsSelectLocator(configureFieldsDialogClass);
        val newConfigureFieldsUi = driver.isElementPresent(selectLocator)

        try {
            openConfigureFieldsDialog(dialogLocator)
        } catch (e: TimeoutException) {
            //we probably sometimes click too fast, but no idea what we should wait for
            openConfigureFieldsDialog(dialogLocator)
        }

        if (newConfigureFieldsUi) {
            driver
                .findElements(selectLocator)
                .singleOrNull()
                ?.let { Select(it) }
                ?.takeIf { it.firstSelectedOption.text != "All Fields" }
                ?.let {
                    it.selectByVisibleText("All Fields")
                    driver.wait(elementToBeClickable(selectLocator))
                }
        } else {
            val allFieldsLinkLocator = getVisibleAllFieldsLinkLocator(configureFieldsDialogClass)
            if (driver.isElementPresent(allFieldsLinkLocator)) {
                driver.wait(elementToBeClickable(allFieldsLinkLocator)).click()
                val newAllFieldsLinkLocator = getVisibleAllFieldsLinkLocator(configureFieldsDialogClass)
                driver.wait(not(elementToBeClickable(newAllFieldsLinkLocator)))
            }
        }

        dismissConfigureFieldsDialog()
        return this
    }

    private fun getVisibleAllFieldsLinkLocator(dialogClass: String): By {
        val sectionIndex =
            if (driver.findElement(By.cssSelector(".$dialogClass dl:nth-of-type(1)")).isDisplayed) 1 else 2
        return By.xpath("//div[@class='$dialogClass']//dl[$sectionIndex]//dd[1]//a")
    }

    private fun getFieldsSelectLocator(dialogClass: String): By {
        return By.cssSelector(".$dialogClass #configure-fields");
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

    private fun dismissConfigureFieldsDialog() {
        driver.wait(elementToBeClickable(By.xpath("//*[@id='create-issue-dialog']//h2"))).click()
    }

    fun fillRequiredFields(): IssueCreateDialog {
        form.fillRequiredFields()
        return this
    }

    fun submit() {
        driver.wait(elementToBeClickable(By.id("create-issue-submit"))).click()
        waitForDialogToHide()
    }

    private fun waitUntilSummaryIsFocused() {
        driver.wait(elementIsFocused(By.id("summary")))
    }

    private fun elementIsFocused(locator: By): ExpectedCondition<WebElement?> {
        return object : ExpectedCondition<WebElement?> {
            override fun apply(driver: WebDriver?): WebElement? {
                return elementToBeClickable(locator)
                    .apply(driver)
                    ?.also { it.click() }
                    ?.takeIf { it == driver!!.switchTo().activeElement() }
            }

            override fun toString(): String {
                return "element to be focused: $locator"
            }
        }
    }

}
