package com.team2052.frckrawler.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import com.team2052.frckrawler.bluetooth.client.CompanionDeviceServerDiscoveryStrategy
import com.team2052.frckrawler.bluetooth.client.NoOpServerDiscoverStrategy
import com.team2052.frckrawler.bluetooth.client.ServerDiscoveryStrategy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Optional
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {
  @Singleton
  @Provides
  fun provideBluetoothManager(
    @ApplicationContext context: Context
  ): Optional<BluetoothManager> {
    return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
      val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
      Optional.of(manager)
    } else {
      Optional.empty()
    }
  }

  @Singleton
  @Provides
  fun provideBluetoothAdapter(
    manager: Optional<BluetoothManager>
  ): Optional<BluetoothAdapter> {
    return if (manager.isPresent) {
      Optional.of(manager.get().adapter)
    } else {
      Optional.empty()
    }
  }

  @Singleton
  @Provides
  fun provideServerDiscoveryStrategy(
    @ApplicationContext context: Context
  ): ServerDiscoveryStrategy {
    return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
      CompanionDeviceServerDiscoveryStrategy()
    } else {
      NoOpServerDiscoverStrategy
    }
  }
}