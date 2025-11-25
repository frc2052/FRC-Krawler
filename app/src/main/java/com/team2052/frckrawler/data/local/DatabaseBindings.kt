package com.team2052.frckrawler.data.local

import android.content.Context
import androidx.room.Room
import com.team2052.frckrawler.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object DatabaseBindings {
  @Provides
  @SingleIn(AppScope::class)
  fun provideFrcKrawlerDatabase(
    @ApplicationContext context: Context
  ): FRCKrawlerDatabase {
    return Room.databaseBuilder(
      context,
      FRCKrawlerDatabase::class.java,
      "frckrawler.db"
    ).build()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideMetricsSetDao(
    database: FRCKrawlerDatabase
  ): MetricSetDao {
    return database.metricsSetDao()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideMetricsDao(
    database: FRCKrawlerDatabase
  ): MetricDao {
    return database.metricDao()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideGameDao(
    database: FRCKrawlerDatabase
  ): GameDao {
    return database.gameDao()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideEventDao(
    database: FRCKrawlerDatabase
  ): EventDao {
    return database.eventDao()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideTeamAtEventDao(
    database: FRCKrawlerDatabase
  ): TeamAtEventDao {
    return database.teamAtEventDao()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideMetricDatumDao(
    database: FRCKrawlerDatabase
  ): MetricDatumDao {
    return database.metricDatumDao()
  }
}

