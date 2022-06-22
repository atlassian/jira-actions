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

    // the duration is set to such value because viewing issue with many comments is a heavy operation, and we assume
    // the user is patient enough to wait for all comments to show up
    private val duration: Duration = Duration.ofMinutes(2)
    private val loadMoreCommentsJira8Locator: By = By.cssSelector("a.show-more-comments")
    private val loadMoreCommentsJira9Locator: By = By.cssSelector("button.show-more-comment-tabpanel")
    private val attemptLimit = 5

    fun waitForActive(): CommentTabPanel {
        driver.wait(
            duration,
            ExpectedConditions.elementToBeClickable(By.id("comment-tabpanel"))
        ).click()
        driver.wait(
            duration,
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
            duration,
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
            logger.debug("The number of comments is less than ten or this Jira version is not supported.")
        }
        return this
    }

    private fun showAllCommentsInJira8(): CommentTabPanel {
        val loadMoreButton = driver.findElement(loadMoreCommentsJira8Locator)
        loadMoreButton.click()
        driver.wait(
            duration,
            ExpectedConditions.stalenessOf(loadMoreButton)
        )
        return this
    }

    private fun showAllCommentsInJira9(): CommentTabPanel {
        repeat(attemptLimit) {
            if (!driver.isElementPresent(loadMoreCommentsJira9Locator)) {
                return this
            }
            val loadMoreButton = driver.findElement(loadMoreCommentsJira9Locator)
            Actions(driver)
                .keyDown(Keys.SHIFT)
                .click(loadMoreButton)
                .keyUp(Keys.SHIFT)
                .build()
                .perform()
            driver.wait(
                duration,
                ExpectedConditions.stalenessOf(loadMoreButton)
            )
        }
        return this
    }
}
