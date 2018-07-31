package com.atlassian.performance.tools.jiraactions.page.form


interface FormField {
    fun hasValue(): Boolean
    fun fillWithAnyValue()
}