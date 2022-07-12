package com.team2052.frckrawler.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MatchMetric(
    @PrimaryKey(
        autoGenerate = true
    ) val id: Int = 0,
    val name: String
)