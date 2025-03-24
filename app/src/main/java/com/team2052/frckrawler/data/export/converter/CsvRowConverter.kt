package com.team2052.frckrawler.data.export.converter

import com.team2052.frckrawler.data.model.Metric

/**
 * Convert metrics & data to CSV rows (as a string)
 */
interface CsvRowConverter<T> {
  /**
   * Get the header for the CSV export
   *
   * `metrics` must be sorted so that the metrics are in the same
   * order as they exist in each `CsvDataRow` passed to [getDataRow]
   */
  fun getHeader(metrics: List<Metric>): String

  /**
   * Get a single row of data for the CSV export
   *
   * The list of `MetricDatum` in `csvDataRow` must be sorted in the same order as the metrics
   * passed to [getHeader]
   */
  fun getDataRow(
    csvDataRow: T
  ): String
}