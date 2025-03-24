package com.team2052.frckrawler.data.summary

import junit.framework.TestCase.assertEquals
import org.junit.Test

class OptionPercentageSummaryValueTest {
  @Test
  fun `single option display string`() {
    val value = OptionPercentageSummaryValue(
      values = mapOf(
        "Option 1" to 100.0
      )
    )
    assertEquals("Option 1 - 100.0%", value.asDisplayString())
  }

  @Test
  fun `multi option display string`() {
    val value = OptionPercentageSummaryValue(
      values = mapOf(
        "Option 1" to 100.0,
        "Option 2" to 20.0
      )
    )
    assertEquals("Option 1 - 100.0%\nOption 2 - 20.0%", value.asDisplayString())
  }
}