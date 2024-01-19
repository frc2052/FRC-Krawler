package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
  tableName = "team_at_event",
  primaryKeys = ["number", "eventId"],
  foreignKeys = [
    ForeignKey(
      entity = Event::class,
      parentColumns = ["id"],
      childColumns = ["eventId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("eventId")]
)
data class TeamAtEvent(
  val number: String,
  val name: String,
  val eventId: Int
)