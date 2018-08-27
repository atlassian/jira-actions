package com.atlassian.performance.tools.jiraactions.api.memories.adaptive.jql

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.page.IssuePage

/**
 * Look at the Issue Page and try to build a jql based on information that can be observed.
 * If this returns <code>null</code>, this means more observations need to be made in order
 * to produce the jql. If this returns a non-null value, the object should be discarded,
 * subsequent calls are undefined.
 * @return a jql query as a <code>String</code> if successfully rendered, <code>null</code> otherwise.
 */
typealias JqlPrescription = (IssuePage) -> String?


object JqlPrescriptions {

    val previousReporters: JqlPrescription = { page: IssuePage ->
        page.getReporter()?.let { "reporter was $it order by description" }
    }

    fun prioritiesInEnumeratedList(seededRandom: SeededRandom): JqlPrescription = { page: IssuePage ->
        page.getPossiblePriorities()
            ?.let { seededRandom.pickMany(it, 3) }
            ?.joinToString()
            ?.let { "priority in ($it) order by reporter" }
    }

    val specifiedAssignee: JqlPrescription = { page: IssuePage ->
        page.getAssignee()?.let { "assignee = $it order by project" }
    }

    val specifiedAssigneeInSpecifiedProject: JqlPrescription = { page: IssuePage ->
        val userName = page.getAssignee()
        val project = page.getProject()
        if (userName != null && project != null) {
            "project = ${project.key} and assignee = $userName order by reporter"
        } else {
            null
        }
    }

    val specifiedProject: JqlPrescription = { page: IssuePage ->
        page.getProject()?.let { "project = ${it.key} order by status" }
    }

    /**
     * Creates a JQL based on the words used in fields of this issue.
     * What we need is to pick a word that will get us a non-empty jql, preferably that will return more issues in
     * the search result.
     * Warning! Current implementation will work only with locales that use Latin alphabet.
     */
    fun filteredByGivenWord(random: SeededRandom): JqlPrescription = { page: IssuePage ->
        val whitespacesPattern = Regex("\\s")
        val lettersOnly = Regex("\\w+")

        // in order to find a word that's neither long or short, with a chance of it being just a regular word,
        // we're looking for a word with three vowels.
        val description = page.getDescription()
        val summary = page.getSummary()

        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'y')
        fun numVowels(s: String): Int {
            return s.toCharArray().asSequence().filter { vowels.contains(it) }.count()
        }

        "$description $summary"
            .split(whitespacesPattern)
            .filter { it.isNotBlank() }
            .shuffled(random.random)
            .asSequence()
            .filter { it.matches(lettersOnly) }
            .filter { numVowels(it) == 3 }
            .firstOrNull()
            ?.let { """text ~ "$it" order by key""" }
    }

    fun groupReporterBelongsTo(random: SeededRandom): JqlPrescription = { page: IssuePage ->
        page.visitReporterProfile()
            ?.getUserGroups()
            ?.let { random.randomOrNull(it) }
            ?.let { "reporter in membersOf(\"$it\") order by project, status" }
    }
}