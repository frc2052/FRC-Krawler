package com.team2052.frckrawler.data.summary

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.model.Metric

interface MetricSummarizer {
  fun summarize(metric: Metric, data: List<MetricDatum>): SummaryValue
}

fun Metric.getSummarizer(): MetricSummarizer {
  return when (this) {
    is Metric.BooleanMetric -> BooleanMetricSummarizer
    is Metric.CounterMetric -> NumericMetricSummarizer
    is Metric.SliderMetric -> NumericMetricSummarizer
    is Metric.ChooserMetric -> StringOptionsMetricSummarizer
    is Metric.CheckboxMetric -> StringOptionsMetricSummarizer
    is Metric.StopwatchMetric -> StringOptionsMetricSummarizer
    is Metric.TextFieldMetric -> FullStringMetricSummarizer
    is Metric.SectionHeader -> NoOpSummarizer
  }
}