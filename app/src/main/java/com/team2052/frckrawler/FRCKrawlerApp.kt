package com.team2052.frckrawler

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.lang.ref.WeakReference

@HiltAndroidApp
class FRCKrawlerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = WeakReference(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        // Context is wrapped in weak reference to avoid memory leaks
        private lateinit var context: WeakReference<Context>

        // This function is used by the Screen class to fetch the titles of screens.
        // Because this function is only called after the creation of this class
        // it's safe to assume that the context has been instantiated.
        fun getString(@StringRes resId: Int): String {
            val string = context.get()?.getString(resId)
            // Because this method will only be handling translations
            // it's safe to return an empty string in null situations.
            return string ?: ""
        }
    }
}