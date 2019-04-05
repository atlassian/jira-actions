package com.atlassian.performance.tools.jiraactions.api.page.form

import com.atlassian.performance.tools.jiraactions.api.page.wait
import com.atlassian.performance.tools.jiraactions.page.form.*
import org.openqa.selenium.By
import org.openqa.selenium.By.xpath
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration
import java.util.function.Supplier

class IssueForm(
    private val formLocator: By,
    private val driver: WebDriver
) {
    private val requiredFieldGroupsLocator = xpath("//span[contains(@class,'icon-required')]/ancestor::div[contains(@class,'field-group')]")

    fun <T> waitForRefresh(
        action: Supplier<T>
    ): T {
        val form = getForm()
        val result = action.get()
        driver.wait(Duration.ofSeconds(30), ExpectedConditions.invisibilityOf(form))
        return result
    }

    fun fillRequiredFields(): IssueForm {
        val form = getForm()
        val formFieldFactory = FormFieldFactory(
            driver,
            listOf(
                TextInput.Descriptor(),
                ComboBox.Descriptor(),
                TextSelectField.Descriptor(),
                Select.Descriptor(),
                TextArea.Descriptor()
            )
        )
        form.findElements(requiredFieldGroupsLocator)
            .filter { it.isDisplayed }
            .map { formFieldFactory.getFormField(it) }
            .filter { !it.hasValue() }
            .forEach { it.fillWithAnyValue() }
        return this
    }

    private fun getForm() = driver.findElement(formLocator)
}