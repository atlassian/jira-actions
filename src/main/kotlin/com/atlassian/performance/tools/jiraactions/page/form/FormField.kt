package com.atlassian.performance.tools.jiraactions.page.form


internal interface FormField {
    fun hasValue(): Boolean
    fun fillWithAnyValue()
}