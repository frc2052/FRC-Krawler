package com.team2052.frckrawler.ui.navigation

import android.os.Bundle
import androidx.compose.runtime.SideEffect
import androidx.core.os.bundleOf
import androidx.navigation3.runtime.NavEntryDecorator
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

fun <T: Any> rememberFirebaseScreenDecorator(): FirebaseScreenDecorator<T> {
    return FirebaseScreenDecorator()
}

class FirebaseScreenDecorator<T: Any> : NavEntryDecorator<T>(
  decorate = { entry ->
    SideEffect {
      val bundle = bundleOf(
        FirebaseAnalytics.Param.SCREEN_NAME to entry.contentKey
      )
      println("screen view ${entry.contentKey}")
      Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
    entry.Content()
  }
)