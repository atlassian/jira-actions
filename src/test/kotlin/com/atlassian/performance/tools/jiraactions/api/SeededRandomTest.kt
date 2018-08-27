package com.atlassian.performance.tools.jiraactions.api

import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Test

class SeededRandomTest {

    @Test
    fun shouldGenerateTheSameNumbers() {
        val numbers = (1..16).map {
            SeededRandom(89273423).random.nextInt()
        }

        val anyOfTheNumbers = numbers.first()
        assertThat(numbers, everyItem(equalTo(anyOfTheNumbers)))
    }

    @Test
    fun shouldGenerateDifferentNumbers() {
        val alpha = SeededRandom(32409823).random.nextInt()
        val beta = SeededRandom(7475212423).random.nextInt()

        assertThat(alpha, not(equalTo(beta)))
    }

    @Test
    fun shouldBePredictable() {
        val random = SeededRandom(2879423).random
        val predictedNumbers = listOf(
            1942083369,
            1529352435,
            -267016511,
            852781497,
            -1626000199,
            346481482,
            2087286012,
            -1312328522,
            -822264425,
            -1502150971,
            -854193264,
            1587084262,
            1090067452,
            -672670705,
            1565094932,
            -1958632506
        )

        val generatedNumbers = (1..16).map { random.nextInt() }

        assertThat(generatedNumbers, equalTo(predictedNumbers))
    }
}