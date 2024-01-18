package com.team2052.frckrawler.data.sync

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

fun List<MetricRecord>.toPackets(): List<MetricPacket> {
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