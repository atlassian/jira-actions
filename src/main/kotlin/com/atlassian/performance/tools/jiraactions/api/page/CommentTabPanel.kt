package com.atlassian.performance.tools.jiraactions.api.page

import com.atlassian.performance.tools.jiraactions.api.memories.Comment
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class CommentTabPanel(
    val driver: WebDriver
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    private val loadMoreCommentsJira8Locator: By = By.cssSelector("a.show-more-comments")
    private val loadMoreCommentsJira9Locator: By = By.cssSelector("button.show-more-comment-tabpanel")

    fun waitForActive(): CommentTabPanel {
        driver.wait(
            Duration.ofSeconds(45),
            ExpectedConditions.elementToBeClickable(By.id("comment-tabpanel"))
        ).click()
        driver.wait(
            Duration.ofSeconds(60),
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("#comment-tabpanel.active-tab"))
        )
        return this
    }

    fun getComments(): Set<Comment> {
        val comments = driver.findElements(By.cssSelector("#issue_actions_container > div.activity-comment"))
        return comments.map {
            val id = it.getAttribute("id").split("-")[1]
            val url = it.findElement(By.cssSelector(".comment-created-date-link")).getAttribute("href")
            Comment(id, url)
        }.toSet()
    }

    fun validateCommentIsFocused(
        commentId: String
    ): CommentTabPanel {
        driver.wait(
            Duration.ofSeconds(30),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#comment-$commentId.focused"))
        )
        return this
    }

    fun getIssueKey(): String = driver.findElement(By.cssSelector("a.issue-link")).getAttribute("data-issue-key")

    fun showAllComments(): CommentTabPanel {
        if (driver.isElementPresent(loadMoreCommentsJira8Locator)) {
            return showAllCommentsInJira8()
        } else if (driver.isElementPresent(loadMoreCommentsJira9Locator)) {
            return showAllCommentsInJira9()
        } else {
            logger.debug("This Jira version is not supported.")
        }
        return this
    }

    private fun showAllCommentsInJira8(): CommentTabPanel {
        val loadMoreButton = driver.findElement(loadMoreCommentsJira8Locator)
        loadMoreButton.click()
        driver.wait(
            timeout = Duration.ofSeconds(60),
            condition = ExpectedConditions.stalenessOf(loadMoreButton)
        )
        return this
    }

    private fun showAllCommentsInJira9(): CommentTabPanel {
        driver.findElement(loadMoreCommentsJira9Locator).click()

        if (driver.isElementPresent(loadMoreCommentsJira8Locator) && driver.findElement(loadMoreCommentsJira9Locator)
                .getAttribute("data-load-all-enabled").equals("true")
        ) {
            val loadMoreButton = driver.findElement(loadMoreCommentsJira9Locator)

            Actions(driver)
                .keyDown(Keys.SHIFT)
                .click(loadMoreButton)
                .keyUp(Keys.SHIFT)
                .build()
                .perform()
            driver.wait(
                timeout = Duration.ofSeconds(60),
                condition = ExpectedConditions.stalenessOf(loadMoreButton)
            )
        }
        return this
    }
}
