package com.team2052.frckrawler.di

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import com.team2052.frckrawler.di.viewmodel.ViewModelGraph
import com.team2052.frckrawler.di.work.WorkProviders
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface AppGraph : WorkProviders, ViewModelGraph.Factory {

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(@Provides application: Application): AppGraph
  }

  /*
   * Allow Metro to inject Activities and Services via FrcKrawlerAppComponentFactory
   */
  @Multibinds val activityProviders: Map<KClass<out Activity>, Provider<Activity>>
  @Multibinds val serviceProviders: Map<KClass<out Service>, Provider<Service>>

  @Provides
  @ApplicationContext
  fun provideApplicationContext(application: Application): Context = application
}