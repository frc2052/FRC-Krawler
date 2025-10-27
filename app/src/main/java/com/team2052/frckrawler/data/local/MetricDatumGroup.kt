package com.team2052.frckrawler.data.local

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class MetricDatumGroup {
  Match,
  Pit
}