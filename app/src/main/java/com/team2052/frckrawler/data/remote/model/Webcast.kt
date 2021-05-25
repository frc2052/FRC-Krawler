package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Webcast(
    val type: String,
    val channel: String,
    val date: String,
    val `file`: String,
)