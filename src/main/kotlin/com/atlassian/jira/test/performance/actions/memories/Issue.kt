package com.atlassian.jira.test.performance.actions.memories

data class Issue(
    val key: String,
    val id: Long,
    val type: String,
    val editable: Boolean
)