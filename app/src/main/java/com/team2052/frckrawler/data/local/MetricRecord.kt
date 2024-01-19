package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A single metric as it will be stored in the database.
 *
 * This is different from our standard `Metric` class because we are limited in
 * how we can store data in our database. Our `Metric` class is easier to work
 * with in most of our code, this model is easier to store in a database.
 */
@Entity(
  tableName = "metric",
  foreignKeys = [
    ForeignKey(
      entity = MetricSet::class,
      parentColumns = ["id"],
      childColumns = ["metricSetId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("metricSetId")]
)
data class MetricRecord(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val name: String,
  val type: MetricType,
  val priority: Int,
  val enabled: Boolean,
  val metricSetId: Int,
  val options: String?,
)