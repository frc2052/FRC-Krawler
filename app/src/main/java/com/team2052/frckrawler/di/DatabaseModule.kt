package com.team2052.frckrawler.di

import android.content.Context
import androidx.room.Room
import com.team2052.frckrawler.data.local.FRCKrawlerDatabase
import com.team2052.frckrawler.util.Constants.DATABASE_NAME
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
    fun provideFRCKrawlerDatabase(@ApplicationContext context: Context): FRCKrawlerDatabase {
        context.applicationContext.deleteDatabase(DATABASE_NAME)
        return Room.databaseBuilder(
            context,
            FRCKrawlerDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }
}