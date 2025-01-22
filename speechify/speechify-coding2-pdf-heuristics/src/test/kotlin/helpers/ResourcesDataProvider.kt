package org.speechify.tests.helpers

import kotlinx.serialization.json.Json
import org.speechify.models.TextBlock
import org.speechify.models.WordWithBoundingBox
import java.io.File
import java.io.IOException

object ResourcesDataProvider {
    private const val RESOURCE_PATH = "src/test/resources"

    fun getExpectedDataFor(testName: String): List<TextBlock> {
        val jsonString = openFile("$RESOURCE_PATH/expected/$testName.json")
        val blocks: List<TextBlock> = Json.decodeFromString(jsonString)
        return blocks
    }

    fun getInputDataFor(testName: String): List<WordWithBoundingBox> {
        val jsonString = openFile("$RESOURCE_PATH/input/$testName.json")
        val items: List<WordWithBoundingBox> = Json.decodeFromString(jsonString)
        return items
    }

    private fun openFile(path: String): String =
        try {
            File(path).readText()
        } catch (e: IOException) {
            error("File can't be found.")
        }
}
