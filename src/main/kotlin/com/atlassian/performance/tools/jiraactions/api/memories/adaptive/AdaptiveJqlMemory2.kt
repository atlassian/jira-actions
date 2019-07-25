package com.atlassian.performance.tools.jiraactions.api.memories.adaptive

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.jql.Jql
import com.atlassian.performance.tools.jiraactions.api.jql.JqlSupplier
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory2
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql.JqlPrescription
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql.JqlPrescriptions
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage
import com.atlassian.performance.tools.jiraactions.jql.BuiltInJQL
import com.atlassian.performance.tools.jiraactions.jql.JqlContextImpl
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Predicate

class AdaptiveJqlMemory2(
    private val random: SeededRandom
) : JqlMemory2 {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    companion object {
        fun getBuiltInJqlSuppliers(): List<JqlSupplier> = BuiltInJQL.values().toList()
    }

    private val preparedQueries = mutableListOf<Jql>();

    init {
        BuiltInJQL.RESOLVED.get(JqlContextImpl(random))?.let { preparedQueries.add(it) }
        BuiltInJQL.GENERIC_WIDE.get(JqlContextImpl(random))?.let { preparedQueries.add(it) }
    }

    private val availableJqlSuppliers = mutableSetOf(
        BuiltInJQL.PRIORITIES,
        BuiltInJQL.PROJECT,
        BuiltInJQL.ASSIGNEE,
        BuiltInJQL.REPORTERS,
        BuiltInJQL.PROJECT_ASSIGNEE,
        BuiltInJQL.GIVEN_WORD
    )

    override fun observe(issuePage: IssuePage) {
        val jql = availableJqlSuppliers.asSequence()
            .map { it.get(JqlContextImpl(random, issuePage)) }
            .filter { it != null }
            .firstOrNull()

        jql?.let {
            logger.debug("Rendered a new jql query: <<${it.query()!!}>>")
            preparedQueries.add(it)
            availableJqlSuppliers.remove(it.supplier())
        }
    }

    override fun recall(): Jql? {
        return random.pick(preparedQueries)
    }

    override fun remember(memories: Collection<Jql>) {
        preparedQueries.addAll(memories)
    }

    override fun recall(filter: Predicate<Jql>): Jql? {
        return random.pick(preparedQueries.filter {filter.test(it) }.toList())
    }
}

