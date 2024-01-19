package com.team2052.frckrawler.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkModule {
  @Singleton
  @Provides
  fun provideWorkManager(
    @ApplicationContext context: Context
  ): WorkManager {
    return WorkManager.getInstance(context)
  }
}