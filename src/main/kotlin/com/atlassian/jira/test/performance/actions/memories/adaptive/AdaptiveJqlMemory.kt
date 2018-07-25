package com.atlassian.jira.test.performance.actions.memories.adaptive

import com.atlassian.jira.test.performance.actions.SeededRandom
import com.atlassian.jira.test.performance.actions.memories.JqlMemory
import com.atlassian.jira.test.performance.actions.memories.adaptive.jql.JqlPrescription
import com.atlassian.jira.test.performance.actions.memories.adaptive.jql.JqlPrescriptions
import com.atlassian.jira.test.performance.actions.page.IssuePage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class AdaptiveJqlMemory(
        private val random: SeededRandom
) : JqlMemory {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    private val jqls = mutableListOf(
            "resolved is not empty order by description",
            "text ~ \"a*\" order by summary"
    )
    private val jqlPrescriptions = mutableSetOf(
            JqlPrescriptions.prioritiesInEnumeratedList(random),
            JqlPrescriptions.specifiedProject,
            JqlPrescriptions.specifiedAssignee,
            JqlPrescriptions.previousReporters,
            JqlPrescriptions.specifiedAssigneeInSpecifiedProject,
            JqlPrescriptions.filteredByGivenWord(random)
    )

    override fun observe(issuePage: IssuePage) {
        val bakedJql = jqlPrescriptions.asSequence()
            .map { BakedJql(it, it(issuePage)) }
            .filter { it.jql != null }
            .firstOrNull()

        bakedJql?.let {
            logger.debug("Rendered a new jql query: <<${it.jql!!}>>")
            jqls.add(it.jql)
            jqlPrescriptions.remove(it.jqlPrescription)
        }
    }

    override fun recall(): String? {
        return random.pick(jqls)
    }

    override fun remember(memories: Collection<String>) {
        jqls.addAll(memories)
    }
}

data class BakedJql(
    val jqlPrescription: JqlPrescription,
    val jql: String?
)