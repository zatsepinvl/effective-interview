package org.speechify.tests.helpers

import org.speechify.models.TextBlock
import java.io.File

object HtmlComparisonGenerator {
    private const val RESOURCE_PATH = "src/test/resources"
    private val OUTPUT_DIR =
        File(RESOURCE_PATH, "output").also {
            it.mkdirs()
        }

    fun writeFile(
        testName: String,
        text: String,
    ) {
        File(OUTPUT_DIR, "$testName.html").writeText(text)
    }

    fun generateHtmlComparisonFor(
        testName: String,
        expectedResult: List<TextBlock>,
        actualResult: List<TextBlock>,
    ) {
        writeFile(testName, renderHtmlResultsComparison(expectedResult, actualResult))
    }

    fun renderHtmlResults(result: List<TextBlock>): String {
        return result.joinToString("\n") {
            when (it) {
                is TextBlock.Heading -> "<h2>${it.text}</h2>"
                is TextBlock.Paragraph -> "<p>${it.text}</p>"
                is TextBlock.Marginalia -> "<p><i>${it.text}</i></p>"
            }
        }
    }

    fun renderHtmlResultsComparison(
        expectedResult: List<TextBlock>,
        actualResult: List<TextBlock>,
    ): String {
        return """
            <html><body>
            <div style="display: flex; flex-direction: row">
            <div style="width: 50%">
            <h1>Expected</h1>
            <hr />
            ${renderHtmlResults(expectedResult)}
            </div>
            <div style="width: 50%">
            <h1>Actual</h1>
            <hr />
            ${renderHtmlResults(actualResult)}
            </div>
            </div>
            </body></html>
            """.trimIndent()
    }
}
