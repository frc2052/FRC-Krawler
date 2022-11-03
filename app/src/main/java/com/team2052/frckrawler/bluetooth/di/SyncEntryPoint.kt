package com.team2052.frckrawler.bluetooth.di

import com.team2052.frckrawler.bluetooth.SyncOperationFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SyncEntryPoint {
  fun syncOperationFactory(): SyncOperationFactory
}