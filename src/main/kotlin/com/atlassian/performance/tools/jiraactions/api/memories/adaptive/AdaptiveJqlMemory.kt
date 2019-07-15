package com.atlassian.performance.tools.jiraactions.api.memories.adaptive

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql.JqlPrescription
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql.JqlPrescriptions
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage
import com.atlassian.performance.tools.jiraactions.jql.BuiltInJQL
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Predicate

class AdaptiveJqlMemory(
    private val random: SeededRandom
) : JqlMemory {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    companion object {
        fun getAvailableTags(): List<String> = BuiltInJQL.values().map { it.name }.toList()
    }

    private val jqls = mutableListOf(
        TaggedBakedJql(BakedJql({ _ -> ""}, "resolved is not empty order by description"), BuiltInJQL.RESOLVED.name),
        TaggedBakedJql(BakedJql({ _ -> ""}, "text ~ \"a*\" order by summary"), BuiltInJQL.GENERIC_WIDE.name)
    )

    private val jqlPrescriptions = mutableMapOf(
        BuiltInJQL.PRIORITIES.name to JqlPrescriptions.prioritiesInEnumeratedList(random),
        BuiltInJQL.PROJECT.name to JqlPrescriptions.specifiedProject,
        BuiltInJQL.ASSIGNEE.name to JqlPrescriptions.specifiedAssignee,
        BuiltInJQL.REPORTERS.name to JqlPrescriptions.previousReporters,
        BuiltInJQL.PROJECT_ASSIGNEE.name to JqlPrescriptions.specifiedAssigneeInSpecifiedProject,
        BuiltInJQL.GIVEN_WORD.name to JqlPrescriptions.filteredByGivenWord(random)
    )

    override fun observe(issuePage: IssuePage) {
        val bakedJql = jqlPrescriptions.asSequence()
            .map { TaggedBakedJql(BakedJql(it.value, it.value(issuePage)), it.key) }
            .filter { it.baked.jql != null }
            .firstOrNull()

        bakedJql?.let {
            logger.debug("Rendered a new jql query: <<${it.baked.jql!!}>>")
            jqls.add(it)
            jqlPrescriptions.remove(it.tag)
        }
    }

    override fun recall(): String? {
        return random.pick(jqls)?.baked?.jql
    }

    override fun remember(memories: Collection<String>) {
        jqls.addAll(memories.map { TaggedBakedJql(BakedJql({ _ -> ""}, it)) })
    }

    override fun recallByTag(filter: Predicate<String>): String? {
        return random.pick(jqls.filter {it.tag != null && filter.test(it.tag.toString()) }.toList())?.baked?.jql
    }

    private class TaggedBakedJql(val baked: BakedJql, val tag: String? = null)
}

data class BakedJql(
    val jqlPrescription: JqlPrescription,
    val jql: String?
)