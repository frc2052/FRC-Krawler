package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MetricSet::class,
            parentColumns = ["id"],
            childColumns = ["matchMetricsSetId", "pitMetricsSetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val tbaId: String?,
    val gameId: Int,
    val matchMetricsSetId: Int?,
    val pitMetricsSetId: Int?,
)