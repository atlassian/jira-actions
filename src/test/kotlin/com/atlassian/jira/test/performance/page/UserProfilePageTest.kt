package com.atlassian.jira.test.performance.page

import com.atlassian.jira.test.performance.actions.page.splitTagTextIntoLines
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert
import org.junit.Test

class UserProfilePageTest {

    @Test
    fun splittingMultilineTextShouldSplitOnHtmlTags() {
        Assert.assertThat(splitTagTextIntoLines("A<BR>B"), equalTo(listOf("A", "B")))
    }

    @Test
    fun splittingMultilineTextShouldSplitOnNewlines() {
        Assert.assertThat(splitTagTextIntoLines("A\nC"), equalTo(listOf("A", "C")))
    }

    @Test
    fun splittingTextShouldRemoveBlanks() {
        Assert.assertThat(splitTagTextIntoLines("A<br><br>B"), equalTo(listOf("A", "B")))
    }

    @Test
    fun splittingTextShouldTrim() {
        Assert.assertThat(splitTagTextIntoLines("    A<br>B    "), equalTo(listOf("A", "B")))
    }

    @Test
    fun splittingComplexTextShouldApplyAllRulesWithThisHaikuByMihaelaPijrol() {
        val haiku = "" +
                "a metallic fish<br>" +
                "\nupon celestial vault\n<br>" +
                "           illusion of wings\n\n"
        Assert.assertThat(splitTagTextIntoLines(haiku), equalTo(listOf(
                "a metallic fish",
                "upon celestial vault",
                "illusion of wings")))
    }
}