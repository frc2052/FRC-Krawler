package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  foreignKeys = [
    ForeignKey(
      entity = Game::class,
      parentColumns = ["id"],
      childColumns = ["gameId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index("gameId"),
  ]
)
data class Event(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val gameId: Int,
  val tbaId: String? = null,
) {
  companion object {
    /**
     * This ID is reserved for the current event being
     * used in the scouting client configuration
     */
    const val SCOUT_EVENT_ID = 1
  }
}