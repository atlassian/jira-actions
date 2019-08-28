package com.atlassian.performance.tools.jiraactions.lib

import com.atlassian.performance.tools.io.api.ensureDirectory
import org.apache.logging.log4j.LogManager
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.RemoteWebDriver
import java.io.File
import java.nio.file.Paths
import java.util.*

class WebDriverDiagnostics(
    private val driver: WebDriver,
    private val display: TakesScreenshot
) {

    constructor(
        driver: RemoteWebDriver
    ) : this(
        driver = driver,
        display = driver
    )

    private val logger = LogManager.getLogger(this::class.java)

    fun diagnose(
        exception: Exception
    ) {
        val dump = Paths.get("diagnoses")
            .resolve(UUID.randomUUID().toString())
            .toFile()
            .ensureDirectory()
        logger.error("URL: ${driver.currentUrl}, ${dumpHtml(dump)}, ${saveScreenshot(dump)}", exception)
    }

    private fun dumpHtml(
        dumpDirectory: File
    ): String {
        val htmlDump = File(dumpDirectory, "dump.html")
        htmlDump.bufferedWriter().use { it.write(driver.pageSource) }
        return "HTML dumped at ${htmlDump.path}"
    }

    private fun saveScreenshot(
        dumpDirectory: File
    ): String {
        return try {
            val screenshot = File(dumpDirectory, "screenshot.png")
            val temporaryScreenshot = display.getScreenshotAs(OutputType.FILE)
            val moved = temporaryScreenshot.renameTo(screenshot)
            when {
                moved -> "screenshot saved to ${screenshot.path}"
                else -> "screenshot failed to migrate from ${temporaryScreenshot.path}"
            }
        } catch (e: Exception) {
            logger.error("Failed to take a screenshot", e)
            "screenshot failed"
        }
    }
}
