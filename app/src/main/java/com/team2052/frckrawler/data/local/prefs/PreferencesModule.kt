package com.team2052.frckrawler.data.local.prefs

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {
  @Binds
  abstract fun bindPreferences(
    prefs: FrcKrawlerDatastorePreferences
  ): FrcKrawlerPreferences
}