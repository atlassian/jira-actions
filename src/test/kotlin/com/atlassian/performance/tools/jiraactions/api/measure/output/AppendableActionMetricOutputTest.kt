package com.atlassian.performance.tools.jiraactions.api.measure.output

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult.ERROR
import com.atlassian.performance.tools.jiraactions.api.ActionResult.OK
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.Duration.*
import java.time.Instant.parse
import java.util.*

class AppendableActionMetricOutputTest {

    @JvmField
    @Rule
    val folder = TemporaryFolder()

    @Test
    fun shouldDumpToFile() {
        val uuid1 = UUID.fromString("0e5ead7c-dc9c-4f48-854d-5200a1a71058")
        val uuid2 = UUID.fromString("7609ea12-b926-4761-acf8-e3e0858c5294")
        val metrics = listOf(
            ActionMetric(
                label = "View Dashboard",
                result = OK,
                duration = ofMillis(982),
                start = parse("2017-12-12T10:37:52.749Z"),
                virtualUser = uuid1
            ),
            ActionMetric(
                label = "Edit Issue",
                result = OK,
                duration = ofMillis(1400),
                start = parse("2017-12-12T10:38:04.493Z"),
                virtualUser = uuid2
            ),
            ActionMetric(
                label = "View Dashboard",
                result = ERROR,
                duration = ofMillis(294),
                start = parse("2017-12-12T10:38:36.275Z"),
                virtualUser = uuid2
            ),
            ActionMetric(
                label = "Create Issue",
                result = OK,
                duration = ofMinutes(2) + ofSeconds(26) + ofMillis(786),
                start = parse("2017-12-12T10:38:36.275Z"),
                virtualUser = uuid1
            ),
            ActionMetric(
                label = "View Board",
                result = OK,
                duration = ofSeconds(26) + ofMillis(786),
                start = parse("2017-12-12T10:38:36.277Z"),
                virtualUser = uuid1,
                observation = IssuesOnBoard(5).serialize()
            )
        )
        val file = folder.newFile("results.csv")

        file.bufferedWriter().use { target ->
            val output = AppendableActionMetricOutput(target)
            metrics.forEach { output.write(it) }
        }

        val resultsBody = file.readText().replace("\r\n", "\n")
        val expectedResult = File(javaClass.classLoader.getResource("action-metrics.jpt").file).readText()
        assertEquals(expectedResult, resultsBody)
    }
}