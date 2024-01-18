package com.team2052.frckrawler.data.local.view

import androidx.room.DatabaseView
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricSet

@DatabaseView("""
    SELECT * FROM metricdatum INNER JOIN metric ON metricId = metric.id
        WHERE metricSetId = ${MetricSet.SCOUT_PIT_METRIC_SET_ID}
            OR metricSetId = ${MetricSet.SCOUT_MATCH_METRIC_SET_ID}
        GROUP BY metricSetId
""")
data class RemoteScoutMetrics(
    val dataSets: List<MetricDataSet>
)

data class MetricDataSet(
    val metricSetId: Int,
    val data: List<MetricDatum>
)