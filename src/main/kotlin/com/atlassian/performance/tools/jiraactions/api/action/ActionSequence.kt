package com.atlassian.performance.tools.jiraactions.api.action

class ActionSequence(
    private vararg val actions: Action
) : Action {

    override fun run() {
        actions.forEach { it.run() }
    }
}

fun Action.then(vararg another: Action) = ActionSequence(this, *another)
