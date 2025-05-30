package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  foreignKeys = [
    ForeignKey(
      entity = MetricSet::class,
      parentColumns = ["id"],
      childColumns = ["matchMetricsSetId"],
    ),
    ForeignKey(
      entity = MetricSet::class,
      parentColumns = ["id"],
      childColumns = ["pitMetricsSetId"],
    ),
  ],
  indices = [
    Index("matchMetricsSetId"),
    Index("pitMetricsSetId"),
  ]
)
data class Game(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val matchMetricsSetId: Int? = null,
  val pitMetricsSetId: Int? = null,
) {
  companion object {
    /**
     * This ID is reserved for the current game being
     * used in the scouting client configuration
     */
    const val SCOUT_GAME_ID = 1
  }
}