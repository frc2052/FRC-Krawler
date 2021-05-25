package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IOS(
    @Json(name = "min_app_version") val minAppVersion: Int,
    @Json(name = "latest_app_version") val latestAppVersion: Int,
)