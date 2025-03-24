package com.team2052.frckrawler.data.summary

import junit.framework.TestCase.assertEquals
import org.junit.Test

class RawStringListSummaryValueTest {
  @Test
  fun `single item list display string`() {
    val summary = RawStringListSummaryValue(listOf("Didn't move"))
    assertEquals("Didn't move", summary.asDisplayString())
  }

  @Test
  fun `multi item list display string`() {
    val summary = RawStringListSummaryValue(listOf("Didn't move", "Got red card"))
    assertEquals("Didn't move\nGot red card", summary.asDisplayString())
  }

}