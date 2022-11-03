package com.team2052.frckrawler.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import androidx.work.WorkManager
import com.team2052.frckrawler.bluetooth.client.CompanionDeviceServerDiscoveryStrategy
import com.team2052.frckrawler.bluetooth.client.ScanServerDiscoveryStrategy
import com.team2052.frckrawler.bluetooth.client.ServerDiscoveryStrategy
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