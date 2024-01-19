package com.team2052.frckrawler.data.sync

import com.squareup.moshi.JsonClass
import com.team2052.frckrawler.data.local.MetricType

@JsonClass(generateAdapter = true)
data class MetricPacket(
  val id: String,
  val name: String,
  val type: MetricType,
  val priority: Int,
  val options: String?
)