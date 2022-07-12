package com.team2052.frckrawler.di

import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RealPermissionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionsModule {
    @Binds
    abstract fun bindPermissionManager(
        manager: RealPermissionManager
    ): PermissionManager

}