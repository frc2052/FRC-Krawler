package com.team2052.frckrawler.di

import android.content.Context
import com.team2052.frckrawler.FRCKrawlerApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFRCKrawlerApp(@ApplicationContext app: Context): FRCKrawlerApp =
        app as FRCKrawlerApp

}