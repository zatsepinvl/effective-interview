package org.speechify.tests

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.speechify.parsePage
import org.speechify.tests.helpers.HtmlComparisonGenerator
import org.speechify.tests.helpers.ResourcesDataProvider
import org.speechify.tests.helpers.TestDataList
import kotlin.test.assertEquals

class PdfHeuristicsTest {
    @ParameterizedTest(name = "{0}. PDF parsing test")
    @ValueSource(strings = ["1", "2", "3", "4", "5", "6", "7", "8", "9"])
    //@ValueSource(strings = ["3", "5"])
    fun runPdfHeuristicsTest(testName: String) {
        val items = ResourcesDataProvider.getInputDataFor(testName)
        val actualResult = parsePage(items)
        val expectedResult = ResourcesDataProvider.getExpectedDataFor(testName)

        // in case this is easier to visualize the gaps
        HtmlComparisonGenerator.generateHtmlComparisonFor(testName, expectedResult, actualResult)

        assertEquals(TestDataList(expectedResult), TestDataList(actualResult))
    }
}
