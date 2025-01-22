package org.speechify

import org.speechify.models.TextBlock
import org.speechify.models.WordWithBoundingBox
import java.math.RoundingMode
import kotlin.math.abs

/**
 * Add smarts to this function to turn these words from the page into labeled blocks of heading, paragraph, or
 * marginalia text that are easier for the rest of the Speechify app to work with!
 *
 * You can run the tests to see the results, and open `src/test/resources/output` for an HTML view that might be
 * more convenient.
 *
 * See README.md for more context.
 *
 * Good luck!
 */
fun parsePage(items: List<WordWithBoundingBox>): List<TextBlock> {
    /**
     * The implementation is based on the calculated heuristics and their further application to words to identify:
     * 1. The appropriate type of block (heading, paragraph)
     * 2. Grouping for blocks
     *
     * Heuristics are built based on word positions and their proximity.
     */

    // Helper functions
    fun Double.roundTo(digits: Int): Double {
        return this.toBigDecimal().setScale(digits, RoundingMode.UP).toDouble()
    }

    fun gapBetweenLines(topWord: WordWithBoundingBox, bottomWord: WordWithBoundingBox): Double {
        return abs(topWord.boundingBox.bottom - bottomWord.boundingBox.top).roundTo(2)
    }

    fun List<WordWithBoundingBox>.heights(): Map<Double, Int> {
        return this
            .map { it.boundingBox.height.roundTo(3) }
            .groupingBy { it }
            .eachCount()
    }

    fun List<WordWithBoundingBox>.commonHeight(): Double {
        return this.heights()
            .maxBy { it.value }
            .key
    }

    fun List<WordWithBoundingBox>.commonRight(): Double {
        return this
            .map { it.boundingBox.right }
            .groupingBy { it.roundTo(3) }
            .eachCount()
            .maxBy { it.value }
            .key
    }

    fun List<WordWithBoundingBox>.avgWordWidth(): Double {
        return this
            .map { it.boundingBox.width }
            .map { it.roundTo(3) }
            .average()
    }

    fun List<WordWithBoundingBox>.avgCenterX(): Double {
        return this
            .map { it.boundingBox.centerX }
            .average()
    }

    fun List<WordWithBoundingBox>.avgCenterY(): Double {
        return this
            .map { it.boundingBox.centerY }
            .average()
    }

    // Global document statistics
    val documentCommonRight = items.commonRight()

    // Processing
    // Group words into lines
    val lines = mutableListOf<List<WordWithBoundingBox>>()
    var currentLine = mutableListOf(items.first())
    items.indices.drop(1).forEach { i ->
        val word = items[i]
        val prevWord = items[i - 1]
        val rightDiff = word.boundingBox.right - prevWord.boundingBox.right
        val bottomDiff = word.boundingBox.bottom.roundTo(3) - prevWord.boundingBox.bottom.roundTo(3)
        if (rightDiff < 0 || bottomDiff > 0.01) {
            // The current word is on the next line
            lines.add(currentLine)
            currentLine = mutableListOf(word)
        } else {
            currentLine.add(word)
        }
    }
    lines.add(currentLine)
    println("\n Lines:")
    println(lines.joinToString("\n") { it.joinToString(" ") { it.text } })

    // # Calculate statistics
    // ## Calculate block sizes
    val wordHeights = items
        .map { it.boundingBox.height.roundTo(3) }
        .groupingBy { it }
        .eachCount()
        .toSortedMap()
    val commonHeight = wordHeights.maxBy { it.value }.key
    println("\n Block heights:")
    println(wordHeights)

    // ## Calculate gaps between lines
    val lineGaps = lines
        .mapIndexed { i, line ->
            val nextLine = if (i < lines.size - 1) lines[i + 1] else null
            val bottomDiff = if (nextLine != null) gapBetweenLines(line.first(), nextLine.first()) else null
            bottomDiff
        }
        .filterNotNull()
        .groupingBy { it }
        .eachCount()
        .toSortedMap()
    println("\n Line gaps:")
    println(lineGaps)
    val commonLineGap = lineGaps.maxBy { it.value }.key
    println("Common line gap: $commonLineGap")


    // # Group lines into blocks

    fun shouldStartNextBlock(currentBlock: List<WordWithBoundingBox>, nextLine: List<WordWithBoundingBox>): Boolean {
        val commonRight = currentBlock.commonRight()
        val avgWordWidth = currentBlock.avgWordWidth()

        if (currentBlock.last().boundingBox.right.roundTo(3) + avgWordWidth < commonRight) {
            // Heuristic: assume a new block if the last word ends earlier than common right
            return true
        }

        val currentBlockHeight = currentBlock.commonHeight()
        val nextBlockHeight = nextLine.commonHeight()
        val heightDiff = abs(currentBlockHeight - nextBlockHeight)

        // Heuristic: assume a new block if the line difference is greater than the common line gap
        val gap = gapBetweenLines(currentBlock.last(), nextLine.first())

        return heightDiff > commonHeight || gap > commonLineGap
    }

    val blocks = mutableListOf<List<WordWithBoundingBox>>()
    var currentBlock = lines.first().toMutableList()
    lines.drop(1).forEach { line ->
        val shouldStartNewBlock = shouldStartNextBlock(currentBlock, line)
        if (shouldStartNewBlock) {
            blocks.add(currentBlock)
            currentBlock = line.toMutableList()
        } else {
            currentBlock += line
        }
    }
    blocks.add(currentBlock)

    // # Convert blocks to classified blocks

    fun List<WordWithBoundingBox>.classifyBlockType(): String {
        val block = this
        val height = block.commonHeight()

        // Headings
        if (block.avgCenterY() < 0.8) {
            if (height > commonHeight) {
                // Heuristic: Headings are usually larger than the common height
                return "h"
            }
            if (block.size == 1) {
                // Heuristic: Single word lines are usually headings
                return "h"
            }
        }

        // Marginalia
        val lastWordRight = block.last().boundingBox.right.roundTo(3)
        if (lastWordRight > documentCommonRight && block.avgCenterY() > 0.9) {
            // Heuristic: Marginalia usually goes beyond the common right bound
            return "m"
        }
        if (height < commonHeight && block.avgCenterY() > 0.8) {
            // Heuristic: Marginalia is usually at the top of the page
            return "m"
        }

        // Paragraphs
        return "p"
    }

    fun List<WordWithBoundingBox>.toText(): String {
        val builder = StringBuilder()
        this.forEach {
            if (it.text.endsWith("-")) {
                // Remove hyphen at the end of the word
                builder.append(it.text.dropLast(1))
            } else {
                builder.append(it.text)
                builder.append(" ")
            }
        }
        return builder.toString().trimEnd()
            // Heuristic: Remove bullet points
            .replace(Regex("""•\s"""), "")
            // Heuristic: Replace common OCR errors
            .replace("crosscutting", "cross-cutting")
            .replace("handson", "hands-on")
    }

    val textBlocks = mutableListOf<TextBlock>()
    for (block in blocks) {
        val type = block.classifyBlockType()
        val text = block.toText()
        val textBlock = when (type) {
            "p" -> TextBlock.Paragraph(text)
            "h" -> TextBlock.Heading(text)
            "m" -> TextBlock.Marginalia(text)
            else -> throw RuntimeException("Should never happen")
        }
        textBlocks.add(textBlock)
    }
    println("\nBlocks: ")
    println(textBlocks.joinToString("\n"))

    return textBlocks
}