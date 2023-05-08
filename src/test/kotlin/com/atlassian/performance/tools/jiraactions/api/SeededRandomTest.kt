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
            -1901443050,
            -473909543,
            757202317,
            -933674363,
            -358771904,
            -1748283413,
            1229972701,
            497065728,
            1424659874,
            948144638,
            1393699238,
            1079357318,
            -1358602415,
            -599958027,
            -492069554,
            -1412181367
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
