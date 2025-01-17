package com.team2052.frckrawler.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
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
  @Singleton
  fun provideMetricsSetDao(
    database: FRCKrawlerDatabase
  ): MetricSetDao {
    return database.metricsSetDao()
  }

  @Provides
  @Singleton
  fun provideMetricsDao(
    database: FRCKrawlerDatabase
  ): MetricDao {
    return database.metricDao()
  }

  @Provides
  @Singleton
  fun provideGameDao(
    database: FRCKrawlerDatabase
  ): GameDao {
    return database.gameDao()
  }

  @Provides
  @Singleton
  fun provideEventDao(
    database: FRCKrawlerDatabase
  ): EventDao {
    return database.eventDao()
  }

  @Provides
  @Singleton
  fun provideTeamAtEventDao(
    database: FRCKrawlerDatabase
  ): TeamAtEventDao {
    return database.teamAtEventDao()
  }

  @Provides
  @Singleton
  fun provideMetricDatumDao(
    database: FRCKrawlerDatabase
  ): MetricDatumDao {
    return database.metricDatumDao()
  }
}

