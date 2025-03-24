package com.team2052.frckrawler.data.export

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumGroup
import com.team2052.frckrawler.data.model.Metric
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

fun generateMetric(
  name: String,
  id: String = UUID.randomUUID().toString(),
) = Metric.TextFieldMetric(
  id = id,
  name = name,
  priority = 0,
  enabled = true,
)

fun generateMetricDatum(
  value: String,
  group: MetricDatumGroup = MetricDatumGroup.Match,
  groupNumber: Int = 1,
  metricId: String = "abc",
  teamNumber: String = "2052",
) = MetricDatum(
  value = value,
  lastUpdated = ZonedDateTime.of(
    LocalDate.of(2025, Month.MARCH, 15),
    LocalTime.of(6, 30),
    ZoneId.systemDefault()
  ),
  group = group,
  groupNumber = groupNumber,
  teamNumber = teamNumber,
  metricId = metricId,
  eventId = 0,
)