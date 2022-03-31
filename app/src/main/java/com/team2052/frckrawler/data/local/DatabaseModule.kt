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
        ).fallbackToDestructiveMigration().build()
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
    fun provideMetricsDao(
        database: FRCKrawlerDatabase
    ): MetricsDao {
        return database.metricsDao()
    }
}