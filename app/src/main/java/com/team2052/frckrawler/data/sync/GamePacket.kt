package com.team2052.frckrawler.data.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GamePacket(
    val name: String,

    @Json(name = "match_metrics")
    val matchMetrics: List<MetricPacket>,

    @Json(name = "pit_metrics")
    val pitMetrics: List<MetricPacket>,
)