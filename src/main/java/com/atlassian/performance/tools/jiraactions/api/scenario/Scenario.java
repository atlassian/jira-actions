package com.atlassian.performance.tools.jiraactions.api.scenario;

import com.atlassian.performance.tools.jiraactions.api.SeededRandom;
import com.atlassian.performance.tools.jiraactions.api.WebJira;
import com.atlassian.performance.tools.jiraactions.api.action.Action;
import com.atlassian.performance.tools.jiraactions.api.action.LogInAction;
import com.atlassian.performance.tools.jiraactions.api.action.SetUpAction;
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter;
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory;

import java.util.List;

// This is just a test change

public interface Scenario {
    /**
     * Actions performed when applying load. If some actions need to be more frequent than others,
     * you can supply more of them in the list, e.g. <tt>[A, A, B]</tt> will perform <tt>A</tt> twice as often as <tt>B</tt>.
     *
     * @param jira Navigates Jira via the browser.
     * @param seededRandom Can be used to predictably shuffle the actions themselves or be used to randomize internal action behavior.
     * @param meter Can be used by the actions to measure themselves.
     */
    List<Action> getActions(WebJira jira, SeededRandom seededRandom, ActionMeter meter);

    /**
     * Setup Jira before applying load.
     *
     * @param jira       Helps to navigate to Jira pages.
     * @param meter      Measures login action.
     * @param userMemory The user will be used to log in to the Jira instance
     */
    default Action getLogInAction(WebJira jira, ActionMeter meter, UserMemory userMemory) {
        return new LogInAction(jira, meter, userMemory);
    }

    /**
     * Setup Jira before applying load.
     *
     * @param jira  Helps to navigate to Jira pages. We assume a user is already logged in to Jira instance.
     * @param meter Measures setup action.
     */
    default Action getSetupAction(WebJira jira, ActionMeter meter) {
        return new SetUpAction(jira, meter);
    }
}
