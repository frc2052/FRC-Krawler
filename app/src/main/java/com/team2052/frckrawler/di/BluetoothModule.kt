package com.team2052.frckrawler.di

import android.bluetooth.BluetoothAdapter
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
        bluetooth = BluetoothAdapter.getDefaultAdapter(),
    )
}