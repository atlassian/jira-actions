package com.atlassian.jira.test.performance.actions.page.form


interface FormField {
    fun hasValue(): Boolean
    fun fillWithAnyValue()
}