package com.team2052.frckrawler.data.sync

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TeamPacket(
    val name: String,
    val number: String,
)