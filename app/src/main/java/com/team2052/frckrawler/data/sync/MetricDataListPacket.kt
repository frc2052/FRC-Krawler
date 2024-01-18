package com.team2052.frckrawler.data.sync

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetricDataListPacket(
    val metrics: List<MetricDatumPacket>
)