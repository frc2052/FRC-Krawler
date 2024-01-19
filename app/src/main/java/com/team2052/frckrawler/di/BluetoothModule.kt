package com.team2052.frckrawler.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
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
    if (Build.VERSION.SDK_INT >= 26) {
      return CompanionDeviceServerDiscoveryStrategy()
    } else {
      return ScanServerDiscoveryStrategy(bluetoothAdapter, context)
    }
  }
}