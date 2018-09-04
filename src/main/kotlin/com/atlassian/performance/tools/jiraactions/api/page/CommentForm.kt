package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.time.Duration

class CommentForm(
    private val driver: WebDriver
) {

    private val submitLocator = By.id("comment-add-submit")

    fun waitForButton(): CommentForm {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            timeout = Duration.ofSeconds(6),
            condition = or(
                presenceOfElementLocated(submitLocator),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        driver.tolerateDirtyFormsOnCurrentPage()
        return this
    }

    fun submit(): IssuePage {
        driver.findElement(submitLocator).click()
        return IssuePage(driver)
    }

    fun enterCommentText(
        comment: String
    ): CommentForm {
        driver.findElement(By.id("comment")).sendKeys(comment)
        return this
    }
}