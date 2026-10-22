package com.team2052.frckrawler.ui.components.fields

import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Test

class FRCKrawlerDropdownTest {

  @Test
  fun buildHighlightedText_emptyQuery_returnsUnchangedText() {
    val result = buildHighlightedText("10,000 Lakes Regional", "")
    assertEquals("10,000 Lakes Regional", result.text)
    assertEquals(0, result.spanStyles.size)
  }

  @Test
  fun buildHighlightedText_matchingQuery_boldsMatchedSubstring() {
    val text = "10,000 Lakes Regional"
    val result = buildHighlightedText(text, "lake")

    assertEquals(text, result.text)
    assertEquals(1, result.spanStyles.size)

    val span = result.spanStyles[0]
    // "10,000 " has length 7, "Lake" has length 4
    assertEquals(7, span.start)
    assertEquals(11, span.end)
    assertEquals(FontWeight.Bold, span.item.fontWeight)
  }

  @Test
  fun buildHighlightedText_multipleMatches_boldsAllOccurrences() {
    val text = "Lake Lake Regional"
    val result = buildHighlightedText(text, "lake")

    assertEquals(text, result.text)
    assertEquals(2, result.spanStyles.size)

    assertEquals(0, result.spanStyles[0].start)
    assertEquals(4, result.spanStyles[0].end)
    assertEquals(FontWeight.Bold, result.spanStyles[0].item.fontWeight)

    assertEquals(5, result.spanStyles[1].start)
    assertEquals(9, result.spanStyles[1].end)
    assertEquals(FontWeight.Bold, result.spanStyles[1].item.fontWeight)
  }

  @Test
  fun buildHighlightedText_queryNotPresent_returnsNoSpans() {
    val result = buildHighlightedText("10,000 Lakes Regional", "California")
    assertEquals("10,000 Lakes Regional", result.text)
    assertEquals(0, result.spanStyles.size)
  }
}
