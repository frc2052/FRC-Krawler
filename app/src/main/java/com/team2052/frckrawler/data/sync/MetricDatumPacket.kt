package com.team2052.frckrawler.data.sync

import com.squareup.moshi.JsonClass
import com.team2052.frckrawler.data.local.MetricDatumGroup
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
data class MetricDatumPacket(
  val value: String,
  val lastUpdated: ZonedDateTime,
  val group: MetricDatumGroup,
  val groupNumber: Int,
  val teamNumber: String,
  val metricId: String,
)