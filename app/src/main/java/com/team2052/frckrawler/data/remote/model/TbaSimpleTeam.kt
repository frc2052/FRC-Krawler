package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TbaSimpleTeam(
  @Json(name = "team_number") val number: String,
  val nickname: String,
)