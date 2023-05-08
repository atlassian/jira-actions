package com.atlassian.performance.tools.jiraactions.api

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

class SeededRandom(
    seed: Long = Random().nextLong()
) {

    private val logger: Logger = LogManager.getLogger(this::class.java)
    val random = Random(Random(seed).nextLong())

    init {
        logger.debug("Using seed $seed")
    }

    fun <T> pick(list: List<T>): T? {
        return if (list.isEmpty()) null else list[random.nextInt(list.size)]
    }

    fun <T> pickMany(list: List<T>, howMany: Int): List<T>? {
        return if (list.size < howMany) {
            null
        } else {
            list.shuffled(random).subList(0, howMany)
        }
    }

    fun <T> randomOrNull(list: List<T>): T? {
        return if (list.isNotEmpty()) {
            pick(list)
        } else {
            null
        }
    }
}
