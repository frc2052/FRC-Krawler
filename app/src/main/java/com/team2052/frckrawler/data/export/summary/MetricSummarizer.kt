package com.team2052.frckrawler.data.export.summary

import com.team2052.frckrawler.data.local.MetricDatum

interface MetricSummarizer {
  fun summarize(data: List<MetricDatum>): String
}