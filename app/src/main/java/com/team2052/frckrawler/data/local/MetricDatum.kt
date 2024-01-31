package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

/**
 * A single piece of data entered for a metric, for example the number of points
 * scored by team 123 in match 1
 */
@Entity
data class MetricDatum(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val value: String,
  val lastUpdated: ZonedDateTime,
  val group: MetricDatumGroup,
  val groupNumber: Int, // AKA match number in all current cases
  val teamNumber: String,
  val metricId: String,
  val eventId: Int,
)