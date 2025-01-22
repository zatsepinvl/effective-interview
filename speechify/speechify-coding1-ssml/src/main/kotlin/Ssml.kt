package com.speechify

import java.util.LinkedList

/**
 * SSML (Speech Synthesis Markup Language) is a subset of XML specifically
 * designed for controlling synthesis. You can see examples of how the SSML
 * should be parsed in [com.speechify.SSMLTest] in `src/test/kotlin/Ssml.test.kt`.
 *
 * You may:
 *  - Read online guides to supplement information given in [com.speechify.SSMLTest] to understand SSML syntax.
 *
 * You must not:
 *  - Use XML parsing libraries or the DocumentBuilderFactory. The task should be solved only using string manipulation.
 *  - Read guides about how to code an XML or SSML parser.
 */

/**
 * Parses SSML to a SSMLNode, throwing on invalid SSML
 *
 * <speak>
 *     <p foo="bar">Hello</p>
 * </speak>
 */


fun parseSSML(ssml: String): SSMLNode {
    // [1] - openingTag
    // [2] - slash
    // [3] - tag name
    // [4] - attributes
    // [5] - closingTag
    // [6] - text
    val regexp = Regex("""(<)\s*(/?)(\w+)([^>]*)(>?)|([^<]*)""")
    // speak -> p
    val stack = LinkedList<SSMLNode>()
    var currentNode: SSMLNode? = null
    var rootNode: SSMLNode? = null

    val attributeRegexp = Regex("""([^=]+)=?\s*("?[^"]*"?)""")
    fun parseAttributes(tag: String): List<SSMLAttribute> {
        return attributeRegexp
            .findAll(tag)
            .map { match ->
                val name = match.groupValues[1].trim()
                require(name.isNotEmpty()) { "Attributes could not be parsed" }

                val value = match.groupValues[2]
                require(value.isNotEmpty()) { "Attributes could not be parsed" }
                require(value.startsWith("\"") && value.endsWith("\"")) {
                    "Attributes could not be parsed"
                }


                SSMLAttribute(
                    name = name,
                    value = value.trim { it == '"' }
                )
            }
            .toList()
    }
    for (match in regexp.findAll(ssml)) {
        // Opening tag
        if (match.groupValues[1] == "<" && match.groupValues[2].isEmpty()) {
            require(match.groupValues[5].isNotEmpty()) { "Tags could not be parsed" }

            val tagName = match.groupValues[3]
            val attributesRaw = match.groupValues[4]
            require(!attributesRaw.contains("<") && !attributesRaw.contains(">")) { "Tags could not be parsed" }
            val attributes = parseAttributes(match.groupValues[4])
            require(tagName.isNotEmpty()) { "Tags could not be parsed" }
            if (currentNode == null && stack.isEmpty()) {
                require(tagName == "speak") { "Tags could not be parsed" }
            }
            currentNode?.let { stack.add(it) }
            currentNode = SSMLElement(tagName, attributes, emptyList())
        }

        // Closing tag
        if (match.groupValues[1] == "<" && match.groupValues[2] == "/") {
            require(match.groupValues[5].isNotEmpty()) { "Tags could not be parsed" }

            val closingTag = match.groupValues[3]
            val closedNode = currentNode
            rootNode = closedNode
            requireNotNull(closedNode) { "Tags could not be parsed" }
            require(closedNode is SSMLElement) { "Tags could not be parsed" }
            require(closedNode.name == closingTag) { "Tags could not be parsed" }

            currentNode = stack.pollLast()
            currentNode = (currentNode as? SSMLElement)?.run {
                copy(children = children + closedNode)
            }
        }

        // Text
        if (match.groupValues[6].isNotEmpty()) {
            val text = match.groupValues[6]
            require(!text.contains("<") && !text.contains(">")) {
                "Tags could not be parsed"
            }
            val textNode = SSMLText(unescapeXMLChars(text))

            requireNotNull(currentNode) { "Tags could not be parsed" }
            require(currentNode is SSMLElement) { "Tags could not be parsed" }
            currentNode = currentNode.run {
                copy(children = children + textNode)
            }
        }
    }

    require(rootNode is SSMLElement && rootNode.name == "speak") { "Tags could not be parsed" }
    return rootNode
}

/**
 * Extracts all the human-readable plain-text contained in an SSML node
 */
fun ssmlNodeToText(node: SSMLNode): String {
    val builder = StringBuilder()

    fun nodeToText(node: SSMLNode, builder: StringBuilder) {
        if (node is SSMLElement) {
            node.children.forEach {
                when (it) {
                    is SSMLText -> builder.append(it.text)
                    is SSMLElement -> nodeToText(it, builder)
                }
            }
        }
    }

    nodeToText(node, builder)

    return builder.toString()
}

// Already done for you
fun unescapeXMLChars(text: String) =
    text.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&")

// SSMLNode is a sealed class, which can be either a SSMLTag or a SSMLText
sealed class SSMLNode

data class SSMLElement(
    val name: String,
    val attributes: List<SSMLAttribute>,
    val children: List<SSMLNode>
) : SSMLNode()

// SSMLText is a type alias for String
data class SSMLText(val text: String) : SSMLNode()

data class SSMLAttribute(
    val name: String,
    val value: String
)


