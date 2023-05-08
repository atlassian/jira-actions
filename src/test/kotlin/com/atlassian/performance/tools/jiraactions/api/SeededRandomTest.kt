package com.atlassian.performance.tools.jiraactions.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.Test

class SeededRandomTest {

    @Test
    fun shouldGenerateTheSameNumbers() {
        val numbers = (1..16).map {
            SeededRandom(89273423).random.nextInt()
        }

        val anyOfTheNumbers = numbers.first()
        assertThat(numbers).allSatisfy { assertThat(it).isEqualTo(anyOfTheNumbers) }
    }

    @Test
    fun shouldGenerateDifferentNumbers() {
        val alpha = SeededRandom(32409823).random.nextInt()
        val beta = SeededRandom(7475212423).random.nextInt()

        assertThat(alpha).isNotEqualTo(beta)
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

        assertThat(generatedNumbers).isEqualTo(predictedNumbers)
    }

    @Test
    fun shouldNotBiasFirstPick() {
        val list = listOf("A", "B")

        val picked = (1..200L).map { SeededRandom(it).pick(list) }

        val aRatio = picked.count { it == "A" }.toDouble() / picked.size
        val bRatio = picked.count { it == "B" }.toDouble() / picked.size
        assertThat(aRatio).`as`("A ratio is close to 50%").isCloseTo(0.50, offset(0.10))
        assertThat(bRatio).`as`("B ratio is close to 50%").isCloseTo(0.50, offset(0.10))
    }
}
