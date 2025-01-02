package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TbaSimpleEvent(
  val key: String,
  val name: String,
) {
  companion object {
    fun fake() = TbaSimpleEvent(
      key = "frc201234",
      name = "201234",
    )
  }
}