package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.export.generateMetricDatum
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.summary.NumericMetricSummarizer
import org.junit.Assert.assertEquals
import org.junit.Test

class NumericMetricSummarizerTest {

    @Test
    fun `summarize with normal inputs`() {
        val data = listOf(
            generateMetricDatum("1.0"),
            generateMetricDatum("2.0"),
            generateMetricDatum("4.5")
        )
        val result = NumericMetricSummarizer.summarize(data)
        assertEquals("2.5", result)
    }

    @Test
    fun `summarize with empty values`() {
        val data = listOf(
            generateMetricDatum(""),
            generateMetricDatum("")
        )
        val result = NumericMetricSummarizer.summarize(data)
        assertEquals("", result)
    }

    @Test
    fun `summarize with mixed valid and invalid values`() {
        val data = listOf(
            generateMetricDatum("1.0"),
            generateMetricDatum("invalid"),
            generateMetricDatum("3.0")
        )
        val result = NumericMetricSummarizer.summarize(data)
        assertEquals("2.0", result)
    }

    @Test
    fun `summarize with empty data`() {
        val data = emptyList<MetricDatum>()
        val result = NumericMetricSummarizer.summarize(data)
        assertEquals("", result)
    }
}