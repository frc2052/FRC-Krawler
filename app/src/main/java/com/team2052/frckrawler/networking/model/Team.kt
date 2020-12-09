package com.team2052.frckrawler.networking.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Team(
  @Json(name = "team_number")
  val number: Int,

  val name: String
)