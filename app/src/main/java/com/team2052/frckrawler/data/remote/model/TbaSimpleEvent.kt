package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TbaSimpleEvent(
  val key: String,
  val name: String,
)