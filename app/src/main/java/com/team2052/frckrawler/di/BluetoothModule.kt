package com.team2052.frckrawler.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.team2052.frckrawler.bluetooth.BluetoothController
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
    fun provideBluetoothController(
        @ApplicationContext context: Context
    ) = BluetoothController(
        context = context,
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(),
    )

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
}