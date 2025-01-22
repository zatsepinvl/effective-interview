package com.speechify

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Tests the [parseSSML] and [ssmlNodeToText] functions.
 *
 * Tests can be run in the IDE, or command-line with `./gradlew test`.
 */
internal class SSMLTest {

  /// Tags
  @Test
  fun `should parse tag names`() {
    assertEquals(SSMLElement("speak", emptyList(), emptyList()), parseSSML("<speak></speak>"))
    assertEquals(
      SSMLElement("speak", emptyList(), listOf(SSMLElement("p", emptyList(), emptyList()))),
      parseSSML("<speak><p></p></speak>")
    )
  }

  @ParameterizedTest(name = "parseSSML should throw for {0}")
  @ValueSource(
    strings = [
      /* <speak> [is the root element and is required](https://www.w3.org/TR/speech-synthesis/#S3.1.1) */
      "Hello world",
      "<p>Hello world</p>",
      "<p><speak>Hello world</speak></p>",
      "Hello <speak>world</speak>",
    ]
  )
  fun `should throw on missing speak tag`(ssml: String) {
    assertThrows<IllegalArgumentException> { parseSSML(ssml) }.also {
      assertEquals("Tags could not be parsed", it.message)
    }
  }

  @ParameterizedTest(name = "parseSSML should throw for {0}")
  @ValueSource(
    strings = [
      "<speak>Hello world</speak><foo></foo>",
      "<speak>Hello world</speak>foo",
      "<foo></foo><speak>Hello world</speak>",
      "foo<speak>Hello world</speak>",
    ]
  )
  fun `should throw on multiple top level tags or text`(ssml: String) {
    assertThrows<IllegalArgumentException> { parseSSML(ssml) }.also {
      assertEquals("Tags could not be parsed", it.message)
    }
  }

  @ParameterizedTest(name = "parseSSML should throw for {0}")
  @ValueSource(
    strings = [
      "<speak>Hello world",
      "Hello world</speak>",
      "<speak><p>Hello world</speak>",
      "<speak>Hello world</p></speak>",
      "<speak><p>Hello <s>world</s></speak>",
      "<speak><p>Hello <s>world</p></speak>",
      "<speak><p>Hello <s>world</p></p></speak>",
      "<speak><p>Hello world</s></speak>",
      "<speak><p>Hello world</p></p></speak>",
      "<speak>Hello < world</speak>",
    ]
  )
  fun `should throw on missing or invalid SSML opening and closing tags`(ssml: String) {
    assertThrows<IllegalArgumentException> { parseSSML(ssml) }.also {
      assertEquals("Tags could not be parsed", it.message)
    }
  }

  /// Attributes
  @Test
  fun `should parse tag attributes`() {
    assertEquals(
      SSMLElement("speak", listOf(SSMLAttribute("foo", "")), emptyList()),
      parseSSML("<speak foo=\"\"></speak>")
    )
    assertEquals(
      SSMLElement("speak", listOf(SSMLAttribute("foo", "bar")), emptyList()),
      parseSSML("<speak foo=\"bar\"></speak>")
    )
    assertEquals(
      SSMLElement("speak", listOf(SSMLAttribute("baz:foo", "bar")), emptyList()),
      parseSSML("<speak baz:foo=\"bar\"></speak>")
    )
    assertEquals(
      SSMLElement("speak", listOf(SSMLAttribute("foo", "bar")), emptyList()),
      parseSSML("<speak foo  = \"bar\"></speak>")
    )
    assertEquals(
      SSMLElement("speak", listOf(SSMLAttribute("foo", "bar"), SSMLAttribute("hello", "world")), emptyList()),
      parseSSML("<speak foo  = \"bar\" hello=\"world\"></speak>")
    )
    assertEquals(
      SSMLElement(
        "speak",
        emptyList(),
        listOf(SSMLElement("p", listOf(SSMLAttribute("foo", "bar")), listOf(SSMLText("Hello"))))
      ),
      parseSSML("<speak><p foo=\"bar\">Hello</p></speak>")
    )
  }

  @ParameterizedTest(name = "parseSSML should throw for {0}")
  @ValueSource(
    strings = [
      "<speak foo></speak>",
      "<speak foo=\"bar></speak>",
      /* Quotes are required in XML - https://www.w3.org/TR/xml/#NT-AttValue */
      "<speak foo=bar></speak>",
      "<speak foo=bar\"></speak>",
      "<speak =\"bar\"></speak>"
    ]
  )
  fun `should throw on invalid tag attributes`(ssml: String) {
    assertThrows<IllegalArgumentException> { parseSSML(ssml) }.also {
      assertEquals("Attributes could not be parsed", it.message)
    }
  }

  /// Text
  @Test
  fun `should parse text`() {
    assertEquals(
      SSMLElement("speak", emptyList(), listOf(SSMLText("Hello world"))),
      parseSSML("<speak>Hello world</speak>")
    )
    assertEquals(
      SSMLElement("speak", emptyList(), listOf(SSMLText("Hello"), SSMLElement("p", emptyList(), listOf(SSMLText(" world"))), SSMLText(" foo"))),
      parseSSML("<speak>Hello<p> world</p> foo</speak>")
    )
  }

  @Test
  fun `should unescape XML characters in text`() {
    assertEquals(
      SSMLElement("speak", emptyList(), listOf(SSMLText("TS < JS"))),
      parseSSML("<speak>TS &lt; JS</speak>")
    )
    assertEquals(
      SSMLElement("speak", emptyList(), listOf(SSMLText("TS &< JS"))),
      parseSSML("<speak>TS &amp;&lt; JS</speak>")
    )
    assertEquals(
      SSMLElement("speak", emptyList(), listOf(SSMLElement("p", emptyList(), listOf(SSMLText("TS<"))), SSMLText(" JS"))),
      parseSSML("<speak><p>TS&lt;</p> JS</speak>")
    )
  }

  /// SSMLNodes -> Text
  @Test
  fun `should convert SSML nodes to text`() {
    assertEquals("", ssmlNodeToText(SSMLElement("baz", emptyList(), emptyList())))
    assertEquals("", ssmlNodeToText(SSMLElement("baz", listOf(SSMLAttribute("foo", "bar")), emptyList())))
    assertEquals("Hello world", ssmlNodeToText(SSMLElement("baz", emptyList(), listOf(SSMLText("Hello world")))))
    assertEquals("Hello world", ssmlNodeToText(SSMLElement("baz", listOf(SSMLAttribute("foo", "bar")), listOf(SSMLText("Hello world")))))
    assertEquals(
      "bazHello worldbaz",
      ssmlNodeToText(SSMLElement("baz", listOf(SSMLAttribute("foo", "bar")), listOf(SSMLText("baz"), SSMLElement("p", emptyList(), listOf(SSMLText("Hello world"))), SSMLText("baz"))))
    )
  }
}
