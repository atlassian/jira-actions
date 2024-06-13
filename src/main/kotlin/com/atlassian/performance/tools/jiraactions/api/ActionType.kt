@file:JvmName("ActionTypes")
package com.atlassian.performance.tools.jiraactions.api

import com.atlassian.performance.tools.jiraactions.api.observation.IssueObservation
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.json.JsonObject

/**
 * Represents a kind of user transaction within the application, e.g. visiting a Scrum board.
 * Can be accompanied by an additional observation by the user, e.g. how many issues are on the board.
 * This additional observation potentially could be used to find correlations between data and performance
 * or to target specific parts of the data when exploring the application.
 */
data class ActionType<out T>(
    val label: String,
    private val observationDeserializer: (JsonObject) -> T
) {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    fun deserialize(observation: JsonObject): T? {
        return try {
            observationDeserializer(observation)
        } catch (e: Exception) {
            logger.warn("Failed to parse observation for $label")
            logger.trace(observation, e)
            null
        }
    }
}

@JvmField val ADD_COMMENT = ActionType("Full Add Comment") { Unit }
@JvmField val ADD_COMMENT_SUBMIT = ActionType("Add Comment") { Unit }
@JvmField val BROWSE_BOARDS = ActionType("Browse Boards") { Unit }
@JvmField val BROWSE_PROJECTS = ActionType("Browse Projects") { Unit }
@JvmField val CREATE_ISSUE = ActionType("Full Create Issue") { Unit }
@JvmField val CREATE_ISSUE_SUBMIT = ActionType("Create Issue") { Unit }
@JvmField val EDIT_ISSUE = ActionType("Full Edit Issue") { Unit }
@JvmField val EDIT_ISSUE_SUBMIT = ActionType("Edit Issue") { IssueObservation(it) }
@JvmField val PROJECT_SUMMARY = ActionType("Project Summary") { Unit }
@JvmField val SEARCH_WITH_JQL = ActionType("Search with JQL") { SearchJqlObservation(it) }
@JvmField val SEARCH_JQL_CHANGELOG = ActionType("Changelog searches") { SearchJqlObservation(it) }
@JvmField val SEARCH_WITH_JQL_WILDCARD = ActionType("Wildcard search") { SearchJqlObservation(it) }
@JvmField val SEARCH_JQL_SIMPLE = ActionType("Simple searches") { SearchJqlObservation(it) }
@JvmField val VIEW_ISSUE = ActionType("View Issue") { Unit }
@JvmField val VIEW_DASHBOARD = ActionType("View Dashboard") { Unit }
@JvmField val VIEW_BOARD = ActionType("View Board") { IssuesOnBoard(it) }
@JvmField val LOG_IN = ActionType("Log In") { Unit }
@JvmField val SET_UP = ActionType("Set Up") { Unit }
@JvmField val DISABLE_RTE = ActionType("Disable Rich Text Editor") { Unit }
@JvmField val HIDE_HEALTH_NOTIFICATIONS = ActionType("Hide Instance Health Notifications") { Unit }
@JvmField val VIEW_COMMENT = ActionType("View Comment") { Unit }
@JvmField val VIEW_HISTORY_TAB = ActionType("View History Tab") { IssueObservation(it) }
