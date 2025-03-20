package com.team2052.frckrawler.data.export.aggregator

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricRecord

/**
 * Aggregates metric data into data suitable for exporting to a CSV
 */
interface MetricDataAggregator<T> {
  fun aggregate(
    metrics: List<MetricRecord>,
    data: List<MetricDatum>
  ): List<T>
}