package com.atlassian.performance.tools.jiraactions.api.page.issuenav

import com.atlassian.performance.seleniumjs.NativeExpectedCondition

interface IssueNavResultsView {

    /**
     * Switch to the issue nav results view (like detail or list).
     */
    fun switchToView()

    /**
     * @return condition, which is satisfied if the results view shows results
     */
    fun detectResults(): NativeExpectedCondition

    /**
     * @return total issues found or null if unknown (e.g. search failed)
     */
    fun countResults(): Int?

    /**
     * @return a sample of found issue keys
     */
    fun listIssueKeys(): List<String>
}
