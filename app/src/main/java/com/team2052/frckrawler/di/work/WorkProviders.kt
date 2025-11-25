package com.team2052.frckrawler.di.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import com.team2052.frckrawler.di.ApplicationContext
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import dev.zacsweers.metro.Provider

interface WorkProviders {
  @Provides
  fun provideWorkManager(
    @ApplicationContext context: Context
  ): WorkManager {
    return WorkManager.getInstance(context)
  }

  @Multibinds
  val workerProviders:
    Map<KClass<out ListenableWorker>, Provider<MetroWorkerFactory.WorkerInstanceFactory<*>>>

  val workerFactory: MetroWorkerFactory
}