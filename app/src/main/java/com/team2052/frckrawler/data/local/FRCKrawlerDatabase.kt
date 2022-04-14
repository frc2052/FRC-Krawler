package com.team2052.frckrawler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Game::class,
        MatchMetric::class,
        PitMetric::class
               ],
    version = 1,
    exportSchema = false
)
abstract class FRCKrawlerDatabase: RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun matchMetricsDao(): MatchMetricsDao
    abstract fun pitMetricsDao(): PitMetricsDao
}