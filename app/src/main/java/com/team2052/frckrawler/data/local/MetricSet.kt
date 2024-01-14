package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "metric_set",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("gameId")]
)
data class MetricSet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gameId: Int,
) {
    companion object {
        /**
         * This ID is reserved for the current scouting configuration
         * match metric set
         */
        const val SCOUT_MATCH_METRIC_SET_ID = 1

        /**
         * This ID is reserved for the current scouting configuration
         * pit metric set
         */
        const val SCOUT_PIT_METRIC_SET_ID = 2
    }
}