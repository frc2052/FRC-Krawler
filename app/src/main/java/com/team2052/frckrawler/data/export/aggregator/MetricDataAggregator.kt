package com.team2052.frckrawler.data.export.aggregator

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricRecord

interface MetricDataAggregator<T> {
  fun aggregate(
    metrics: List<MetricRecord>,
    data: List<MetricDatum>
  ): List<T>
}