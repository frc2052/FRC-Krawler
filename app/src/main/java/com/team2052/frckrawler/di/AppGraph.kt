package com.team2052.frckrawler.di

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import com.team2052.frckrawler.bluetooth.server.StopServerBroadcastReceiver
import com.team2052.frckrawler.bluetooth.server.SyncServerService
import com.team2052.frckrawler.di.work.WorkProviders
import com.team2052.frckrawler.ui.MainActivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph, WorkProviders {

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(@Provides application: Application): AppGraph
  }

  @Provides
  @ApplicationContext
  fun provideApplicationContext(application: Application): Context = application

  fun inject(mainActivity: MainActivity)
  fun inject(syncServerService: SyncServerService)
  fun inject(stopReceiver: StopServerBroadcastReceiver)
}