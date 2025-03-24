package com.team2052.frckrawler.data.summary

import junit.framework.TestCase.assertEquals
import org.junit.Test

class EmptySummaryValueTest {
  @Test
  fun `empty value display string`() {
    assertEquals("", EmptySummaryValue.asDisplayString())
  }
}