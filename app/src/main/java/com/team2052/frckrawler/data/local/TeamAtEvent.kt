package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "team_at_event",
    primaryKeys = ["teamNumber", "eventId"],
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TeamAtEvent(
    val teamNumber: Int,
    val eventId: Int
)