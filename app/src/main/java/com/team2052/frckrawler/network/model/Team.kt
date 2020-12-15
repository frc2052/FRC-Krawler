package com.team2052.frckrawler.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Team(
    val name: String,

    @Json(name = "team_number")
    val number: Int
)