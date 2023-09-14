package com.atlassian.performance.tools.jiraactions.api.memories

data class Issue(
    val key: String,
    @Deprecated("Use Issue.key instead")
    val id: Long,
    val type: String,
    val editable: Boolean
)
