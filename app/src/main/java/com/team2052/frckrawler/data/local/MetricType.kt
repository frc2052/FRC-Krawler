package com.team2052.frckrawler.data.local

import androidx.annotation.StringRes
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class MetricType {
  Boolean,
  Counter,
  Slider,
  Chooser,
  Checkbox,
  Stopwatch,
  TextField,
  SectionHeader,
}