package com.team2052.frckrawler.data.sync

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventPacket(
    val name: String,
    val teams: List<TeamPacket>,
)