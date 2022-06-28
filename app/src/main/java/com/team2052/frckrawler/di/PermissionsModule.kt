package com.team2052.frckrawler.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.team2052.frckrawler.bluetooth.BluetoothController
import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RealPermissionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionsModule {
    @Binds
    abstract fun bindPermissionManager(
        manager: RealPermissionManager
    ): PermissionManager

}