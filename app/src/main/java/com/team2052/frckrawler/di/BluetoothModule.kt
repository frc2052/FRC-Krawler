package com.team2052.frckrawler.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.team2052.frckrawler.bluetooth.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object BluetoothModule {

    // TODO: this should be a singleton that persists across the app
    @Provides
    @ViewModelScoped
    fun provideBluetoothController(
        @ApplicationContext context: Context
    ): BluetoothController {
        return BluetoothController(
            context = context,
            bluetooth = BluetoothAdapter.getDefaultAdapter(),
        )
    }

}