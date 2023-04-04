package com.team2052.frckrawler.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import com.team2052.frckrawler.bluetooth.client.discovery.CompanionDeviceServerDiscoveryStrategy
import com.team2052.frckrawler.bluetooth.client.discovery.ScanServerDiscoveryStrategy
import com.team2052.frckrawler.bluetooth.client.discovery.ServerDiscoveryStrategy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {
    @Singleton
    @Provides
    fun provideBluetoothManager(
        @ApplicationContext context: Context
    ): BluetoothManager {
        return context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    @Singleton
    @Provides
    fun provideBluetoothAdapter(
        manager: BluetoothManager
    ): BluetoothAdapter {
        return manager.adapter
    }

    @Singleton
    @Provides
    fun provideServerDiscoveryStrategy(
        bluetoothAdapter: BluetoothAdapter,
        @ApplicationContext context: Context
    ): ServerDiscoveryStrategy {
        return if (Build.VERSION.SDK_INT >= 26) {
            CompanionDeviceServerDiscoveryStrategy()
        } else {
            ScanServerDiscoveryStrategy(bluetoothAdapter, context)
        }
    }
}