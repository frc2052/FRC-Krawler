package com.team2052.frckrawler.data.local.view

//
//@DatabaseView("""
//    SELECT metricdatum.* FROM metricdatum INNER JOIN metric ON metricId = metric.id
//        WHERE metricSetId = ${MetricSet.SCOUT_PIT_METRIC_SET_ID}
//            OR metricSetId = ${MetricSet.SCOUT_MATCH_METRIC_SET_ID}
//""")
//data class RemoteScoutMetrics(
//    val dataSets: List<MetricDatum>
//)