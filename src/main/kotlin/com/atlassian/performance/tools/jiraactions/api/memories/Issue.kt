package com.atlassian.performance.tools.jiraactions.api.memories

data class Issue(
    val key: String,
    val id: Long,
    val type: String,
    val editable: Boolean
)