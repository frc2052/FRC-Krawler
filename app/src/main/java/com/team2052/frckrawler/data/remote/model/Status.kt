package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Status(
    @Json(name = "current_season") val currentSeason: Int,
    @Json(name = "max_season") val maxSeason: Int,
    @Json(name = "is_datafeed_down") val isDatafeedDown: Boolean,
    @Json(name = "down_events") val downEvents: List<String>,
    val ios: IOS,
    val android: Android,
)