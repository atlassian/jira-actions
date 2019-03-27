package com.atlassian.performance.tools.jiraactions.api.parser

import com.atlassian.performance.tools.io.api.directories
import com.atlassian.performance.tools.io.api.ensureDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipFile

class MergingActionMetricsParserTest {

    @Test
    fun shouldStreamDrilldown() {
        val metricsFiles = this::class.java
            .getResource("/QUICK-54.zip")
            .toURI()
            .let { File(it) }
            .let { unzip(it) }
            .directories()
            .map { it.resolve("test-results") }
            .flatMap { it.directories() }
            .map { it.resolve("action-metrics.jpt") }

        val metricsCount = MergingActionMetricsParser().stream(metricsFiles).count()

        assertThat(metricsCount).isEqualTo(20099)
    }

    private fun unzip(
        file: File
    ): File {
        val unpacked = Files.createTempDirectory("apt-jira-actions-test-result").toFile()
        val zip = ZipFile(file)
        zip.stream().forEach { entry ->
            val unpackedEntry = unpacked.resolve(entry.name)
            if (entry.isDirectory) {
                unpackedEntry.ensureDirectory()
            } else {
                zip.getInputStream(entry).use { packedStream ->
                    unpackedEntry.outputStream().use { unpackedStream ->
                        packedStream.copyTo(unpackedStream)
                    }
                }
            }
        }
        return unpacked
    }
}
