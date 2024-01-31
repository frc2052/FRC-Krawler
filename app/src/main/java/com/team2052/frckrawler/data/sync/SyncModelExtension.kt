package com.team2052.frckrawler.data.sync

import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricRecord

fun List<MetricPacket>.toRecords(
  metricSetId: Int
): List<MetricRecord> {
  return map { metric ->
    MetricRecord(
      id = metric.id,
      name = metric.name,
      type = metric.type,
      priority = metric.priority,
      enabled = true,
      metricSetId = metricSetId,
      options = metric.options
    )
  }
}

fun List<MetricRecord>.toMetricPackets(): List<MetricPacket> {
  return map { metric ->
    MetricPacket(
      id = metric.id,
      name = metric.name,
      type = metric.type,
      priority = metric.priority,
      options = metric.options
    )
  }
}

fun List<MetricDatum>.toDatumPackets(): List<MetricDatumPacket> {
  return map { datum ->
    MetricDatumPacket(
      value = datum.value,
      lastUpdated = datum.lastUpdated,
      group = datum.group,
      groupNumber = datum.groupNumber,
      teamNumber = datum.teamNumber,
      metricId = datum.metricId
    )
  }
}

fun List<MetricDatumPacket>.toData(eventId: Int): List<MetricDatum> {
  return map { packet ->
    MetricDatum(
      value = packet.value,
      lastUpdated = packet.lastUpdated,
      group = packet.group,
      groupNumber = packet.groupNumber,
      teamNumber = packet.teamNumber,
      metricId = packet.metricId,
      eventId = eventId,
    )
  }
}