package com.atlassian.performance.tools.jiraactions.api.measure.output

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult.ERROR
import com.atlassian.performance.tools.jiraactions.api.ActionResult.OK
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import com.atlassian.performance.tools.jiraactions.api.w3c.*
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
            ActionMetric.Builder(
                label = "View Dashboard",
                result = OK,
                duration = ofMillis(982),
                start = parse("2017-12-12T10:37:52.749Z")
            )
                .virtualUser(uuid1)
                .build(),
            ActionMetric.Builder(
                label = "Edit Issue",
                result = OK,
                duration = ofMillis(1400),
                start = parse("2017-12-12T10:38:04.493Z")
            )
                .virtualUser(uuid2)
                .build(),
            ActionMetric.Builder(
                label = "View Dashboard",
                result = ERROR,
                duration = ofMillis(294),
                start = parse("2017-12-12T10:38:36.275Z")
            )
                .virtualUser(uuid2)
                .build(),
            ActionMetric.Builder(
                label = "Create Issue",
                result = OK,
                duration = ofMinutes(2) + ofSeconds(26) + ofMillis(786),
                start = parse("2017-12-12T10:38:36.275Z")
            )
                .virtualUser(uuid1)
                .build(),
            ActionMetric.Builder(
                label = "View Board",
                result = OK,
                duration = ofSeconds(26) + ofMillis(786),
                start = parse("2017-12-12T10:38:36.277Z")
            )
                .virtualUser(uuid1)
                .observation(IssuesOnBoard(5).serialize())
                .build(),
            ActionMetric.Builder(
                label = "View Board",
                result = OK,
                duration = ofSeconds(26) + ofMillis(786),
                start = parse("2017-12-12T10:38:36.277Z")
            )
                .virtualUser(uuid1)
                .observation(IssuesOnBoard(6).serialize())
                .build(),
            ActionMetric.Builder(
                label = "Log In",
                result = OK,
                duration = ofSeconds(3) + ofMillis(860),
                start = parse("2018-12-18T16:10:23.088Z")
            )
                .virtualUser(uuid1)
                .drilldown(drilldown)
                .build()
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

    private val drilldown = RecordedPerformanceEntries(
        navigations = listOf(
            PerformanceNavigationTiming(
                resource = PerformanceResourceTiming(
                    entry = PerformanceEntry(
                        name = "http://3.120.138.107:8080/",
                        entryType = "navigation",
                        startTime = ZERO,
                        duration = ofSeconds(1) + ofMillis(740)
                    ),
                    initiatorType = "navigation",
                    nextHopProtocol = "http/1.1",
                    workerStart = ZERO,
                    redirectStart = ofMillis(16),
                    redirectEnd = ofMillis(126),
                    fetchStart = ofMillis(126),
                    domainLookupStart = ofMillis(126),
                    domainLookupEnd = ofMillis(126),
                    connectStart = ofMillis(126),
                    connectEnd = ofMillis(126),
                    secureConnectionStart = ZERO,
                    requestStart = ofMillis(126),
                    responseStart = ofMillis(208),
                    responseEnd = ofMillis(391),
                    transferSize = 12956,
                    encodedBodySize = 11818,
                    decodedBodySize = 59535
                ),
                unloadEventStart = ofMillis(210),
                unloadEventEnd = ofMillis(210),
                domInteractive = ofMillis(805),
                domContentLoadedEventStart = ofMillis(805),
                domContentLoadedEventEnd = ofMillis(830),
                domComplete = ofSeconds(1) + ofMillis(730),
                loadEventStart = ofSeconds(1) + ofMillis(730),
                loadEventEnd = ofSeconds(1) + ofMillis(740),
                type = NavigationType.NAVIGATE,
                redirectCount = 1
            )
        ),
        resources = listOf(
            PerformanceResourceTiming(
                entry = PerformanceEntry(
                    name = "http://3.120.138.107:8080/rest/gadget/1.0/issueTable/jql?num=10&tableContext=jira.table.cols.dashboard&addDefault=true&enableSorting=true&paging=true&showActions=true&jql=assignee+%3D+currentUser()+AND+resolution+%3D+unresolved+ORDER+BY+priority+DESC%2C+created+ASC&sortBy=&startIndex=0&_=1545149426038",
                    entryType = "resource",
                    startTime = ofMillis(903),
                    duration = ofMillis(78)
                ),
                initiatorType = "xmlhttprequest",
                nextHopProtocol = "http/1.1",
                workerStart = ZERO,
                redirectStart = ZERO,
                redirectEnd = ZERO,
                fetchStart = ofMillis(903),
                domainLookupStart = ofMillis(903),
                domainLookupEnd = ofMillis(903),
                connectStart = ofMillis(903),
                connectEnd = ofMillis(903),
                secureConnectionStart = ZERO,
                requestStart = ofMillis(904),
                responseStart = ofMillis(981),
                responseEnd = ofMillis(982),
                transferSize = 3524,
                encodedBodySize = 3032,
                decodedBodySize = 24340
            )
        ),
        elements = listOf(
            PerformanceElementTiming(
                renderTime = ofMillis(1147),
                loadTime = ofMillis(0),
                identifier = "app-header",
                naturalWidth = 0,
                naturalHeight = 0,
                id = "home_link",
                url = ""
            )
        )
    )
}
