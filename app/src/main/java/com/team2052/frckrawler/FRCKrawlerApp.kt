package com.team2052.frckrawler

import android.app.Application
import androidx.work.Configuration
import com.team2052.frckrawler.di.AppGraph
import com.team2052.frckrawler.logging.FirebaseTree
import dev.zacsweers.metro.createGraphFactory
import timber.log.Timber

class FRCKrawlerApp : Application(), Configuration.Provider {

  val appGraph by lazy {
    createGraphFactory<AppGraph.Factory>().create(this)
  }

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
    Timber.plant(FirebaseTree())
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(appGraph.workerFactory)
      .build()
}