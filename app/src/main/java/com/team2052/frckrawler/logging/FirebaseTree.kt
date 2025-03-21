package com.team2052.frckrawler.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class FirebaseTree : Timber.Tree() {
  private val crashlytics = FirebaseCrashlytics.getInstance()

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    if (priority == Log.ERROR || priority == Log.WARN) {
      crashlytics.recordException(
        NonFatalException(
          message = message,
          tag = tag,
          priority = priority,
          cause = t
        )
      )
    }
  }
}