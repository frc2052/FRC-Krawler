package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class District(
    val abbreviation: String,
    @Json(name = "display_name") val displayName: String,
    val key: String,
    val year: Int,
)