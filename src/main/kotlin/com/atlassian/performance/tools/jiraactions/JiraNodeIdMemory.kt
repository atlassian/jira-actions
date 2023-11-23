package com.atlassian.performance.tools.jiraactions

class JiraNodeIdMemory {
    private var nodeId: String? = null

    fun rememberNodeId(nodeId: String) {
        this.nodeId = nodeId
    }

    fun recallNodeId(): String? {
        return nodeId
    }
}
