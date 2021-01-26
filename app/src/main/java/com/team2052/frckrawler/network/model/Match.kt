package com.team2052.frckrawler.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class Match (
    @Json(name = "set_number")
    val setNum: Int,
    @Json(name = "match_number")
    val matchNum: Int,
    val alliances: Alliances,
    @Json(name = "winning_alliance")
    val winningAlliance: String
)
@JsonClass(generateAdapter = true)
data class Alliances(
    val red: Alliance,
    val blue: Alliance
)
@JsonClass(generateAdapter = true)
data class Alliance(
    val score: Int,
    @Json(name = "team_keys")
    val teamKeys: List<String>,
    @Json(name = "surrogate_team_keys")
    val surrogateTeamKeys: List<String>,
    @Json(name = "dq_team_keys")
    val dqTeamKeys: List<String>
)