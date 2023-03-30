package com.atlassian.performance.tools.jiraactions.jql

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.jql.Jql
import com.atlassian.performance.tools.jiraactions.api.jql.JqlContext
import com.atlassian.performance.tools.jiraactions.api.jql.JqlSupplier
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage

internal class JqlImpl(private val query: String,
                       private val supplier: JqlSupplier): Jql {
    override fun query(): String = this.query
    override fun supplier(): JqlSupplier = this.supplier
}

internal class JqlContextImpl(private val rnd: SeededRandom, private val page: IssuePage?) : JqlContext {
    constructor(rnd: SeededRandom) : this(rnd, null)
    override fun seededRandom(): SeededRandom = this.rnd
    override fun issuePage(): IssuePage? = this.page
}

internal enum class BuiltInJQL(private val sup: (JqlContext) -> String?) : JqlSupplier {
    GENERIC_WIDE({ "text ~ \"a*\" order by summary" }),
    RESOLVED({ "resolved is not empty order by description" }),
    PRIORITIES({
        ctx -> ctx.issuePage()?.getPossiblePriorities()
        ?.let { ctx.seededRandom().pickMany(it, 3) }
        ?.joinToString()
        ?.let { "priority in ($it) order by reporter" }
    }),
    PROJECT({
        ctx -> ctx.issuePage()?.getProject()?.let { "project = ${it.key} order by status" }
    }),
    ASSIGNEE({
        ctx -> ctx.issuePage()?.getAssignee()?.let { "assignee = $it order by project" }
    }),
    REPORTERS({
        ctx -> ctx.issuePage()?.getReporter()?.let { "reporter was $it order by description" }
    }),
    PROJECT_ASSIGNEE({
        ctx -> ctx.issuePage()?.let {
        val userName = it.getAssignee()
        val project =  it.getProject()
        if (userName != null && project != null) {
            "project = ${project.key} and assignee = $userName order by reporter"
        } else {
            null
        }
    }

    }),

    /**
     * Creates a JQL based on the words used in fields of this issue.
     * What we need is to pick a word that will get us a non-empty jql, preferably that will return more issues in
     * the search result.
     * Warning! Current implementation will work only with locales that use Latin alphabet.
     */
    GIVEN_WORD({
        ctx -> ctx.issuePage()?.let {
            val whitespacesPattern = Regex("\\s")
            val lettersOnly = Regex("\\w+")

            // in order to find a word that's neither long or short, with a chance of it being just a regular word,
            // we're looking for a word with three vowels.
            val description = it.getDescription()
            val summary = it.getSummary()

            val vowels = setOf('a', 'e', 'i', 'o', 'u', 'y')
            fun numVowels(s: String): Int {
                return s.toCharArray().asSequence().filter { vowels.contains(it) }.count()
            }

            "$description $summary"
                .split(whitespacesPattern)
                .filter { it.isNotBlank() }
                .shuffled(ctx.seededRandom().random)
                .asSequence()
                .filter { it.matches(lettersOnly) }
                .filter { numVowels(it) == 3 }
                .firstOrNull()
                ?.let { """text ~ "$it" order by key""" }
        }
    });

    override fun uniqueName(): String = this.name
    override fun get(context: JqlContext): Jql? = sup.invoke(context)?.let { JqlImpl(it, this) }
}
