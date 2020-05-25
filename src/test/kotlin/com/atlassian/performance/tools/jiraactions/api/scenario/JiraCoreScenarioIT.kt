package com.atlassian.performance.tools.jiraactions.api.scenario

import com.atlassian.performance.tools.dockerinfrastructure.api.browser.DockerisedChrome
import com.atlassian.performance.tools.dockerinfrastructure.api.jira.JiraCoreFormula
import org.junit.Test
import java.nio.file.Paths

class JiraCoreScenarioIT : AbstractJiraCoreScenario() {
    /**
     * During the test, you can connect to WebDriver by the VNC viewer.
     * DockerisedChrome opens the port 5900 so executing command
     * docker ps
     * will show you which port on your host is open and mapped to 5900 on the container.
     * The default password is `secret`.
     */
    @Test
    fun shouldRunScenarioWithoutErrors() {
        JiraCoreFormula.Builder()
            .version(jiraVersion)
            .build()
            .provision().use { jira -> {
                val recordings = Paths.get("build/diagnoses/recordings/" + this::class.java.simpleName)

                DockerisedChrome(recordings).start().use {browser ->
                    shouldRunScenarioWithoutErrors(jira, browser.driver)
                }
            }
            }
    }
}
