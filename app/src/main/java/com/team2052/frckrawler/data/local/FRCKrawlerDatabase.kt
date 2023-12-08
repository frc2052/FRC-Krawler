package com.team2052.frckrawler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Game::class,
        Metric::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class FRCKrawlerDatabase: RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun metricDao(): MetricDao
}