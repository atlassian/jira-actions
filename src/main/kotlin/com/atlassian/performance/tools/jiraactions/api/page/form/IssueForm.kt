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
        val start = System.currentTimeMillis()
        try {
            val form = getForm()
            println("fill req fields0: " + Duration.ofMillis(System.currentTimeMillis()-start))
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
            println("fill req fields1: " + Duration.ofMillis(System.currentTimeMillis()-start))
            val map = form.findElements(requiredFieldGroupsLocator)
                .filter { it.isDisplayed }
                .map { formFieldFactory.getFormField(it) }
            println("fill req fields20: " + Duration.ofMillis(System.currentTimeMillis()-start))
            map
                .filter { !it.hasValue() }
                .forEach { it.fillWithAnyValue() }
        } finally {
            println("fill req fields=: " + Duration.ofMillis(System.currentTimeMillis()-start))
        }

        return this
    }

    private fun getForm() = driver.findElement(formLocator)
}
