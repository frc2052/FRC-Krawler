package com.team2052.frckrawler.di

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Intent
import androidx.annotation.Keep
import androidx.core.app.AppComponentFactory
import com.team2052.frckrawler.FRCKrawlerApp
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

@Keep
class FrcKrawlerAppComponentFactory : AppComponentFactory() {

  private inline fun <reified T : Any> getInstance(
    cl: ClassLoader,
    className: String,
    providers: Map<KClass<out T>, Provider<T>>,
  ): T? {
    val clazz = Class.forName(className, false, cl).asSubclass(T::class.java)
    val modelProvider = providers[clazz.kotlin] ?: return null
    return modelProvider()
  }

  override fun instantiateActivityCompat(
    cl: ClassLoader,
    className: String,
    intent: Intent?
  ): Activity {
    return getInstance(cl, className, activityProviders)
      ?: super.instantiateActivityCompat(cl, className, intent)
  }

  override fun instantiateServiceCompat(
    cl: ClassLoader,
    className: String,
    intent: Intent?
  ): Service {
    return getInstance(cl, className, serviceProviders)
      ?: super.instantiateServiceCompat(cl, className, intent)
  }

  override fun instantiateApplicationCompat(
    cl: ClassLoader,
    className: String
  ): Application {
    val app = super.instantiateApplicationCompat(cl, className)
    val graph = (app as FRCKrawlerApp).appGraph
    activityProviders = graph.activityProviders
    serviceProviders = graph.serviceProviders
    return app
  }

  companion object {
    private lateinit var activityProviders: Map<KClass<out Activity>, Provider<Activity>>
    private lateinit var serviceProviders: Map<KClass<out Service>, Provider<Service>>
  }
}