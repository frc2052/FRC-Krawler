package com.team2052.frckrawler.data.summary

import junit.framework.TestCase.assertEquals
import org.junit.Test

class DoubleSummaryValueTest {
  @Test
  fun `percent value display string`() {
    val value = DoubleSummaryValue(75.0, true)
    assertEquals("75%", value.asDisplayString())
  }

  @Test
  fun `non-percent value display string`() {
    val value = DoubleSummaryValue(2.0, false)
    assertEquals("2", value.asDisplayString())
  }
}