package com.team2052.frckrawler.di

import com.team2052.frckrawler.data.EventRepository
import com.team2052.frckrawler.data.remote.EventService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideEventRepository(
        eventService: EventService
    ): EventRepository = EventRepository(eventService)
}