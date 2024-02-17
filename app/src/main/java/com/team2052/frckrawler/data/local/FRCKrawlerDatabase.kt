package com.team2052.frckrawler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.team2052.frckrawler.data.local.converter.DateTimeConverters

@Database(
  entities = [
    Game::class,
    MetricSet::class,
    MetricRecord::class,
    Event::class,
    TeamAtEvent::class,
    MetricDatum::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class FRCKrawlerDatabase : RoomDatabase() {
  abstract fun metricsSetDao(): MetricSetDao
  abstract fun metricDao(): MetricDao
  abstract fun gameDao(): GameDao
  abstract fun eventDao(): EventDao
  abstract fun teamAtEventDao(): TeamAtEventDao

  abstract fun metricDatumDao(): MetricDatumDao
}