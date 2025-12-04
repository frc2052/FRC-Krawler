package com.team2052.frckrawler.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.team2052.frckrawler.FRCKrawlerApp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.migration.LegacyDatabaseMigration
import com.team2052.frckrawler.ui.migration.LegacyMigrationScreen
import com.team2052.frckrawler.ui.navigation.Navigation
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

class MainActivity : ComponentActivity() {

  @Inject private lateinit var viewModelFactory: MetroViewModelFactory
  @Inject private lateinit var migration: LegacyDatabaseMigration

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // TODO use metrox-android when we go to minSDk 28
    (application as FRCKrawlerApp).appGraph.inject(this)

    // Reset the theme after the splash screen finishes
    setTheme(R.style.Theme_FRCKrawler)

    enableEdgeToEdge(
      navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
    )

    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

    setContent {

      CompositionLocalProvider(LocalMetroViewModelFactory provides viewModelFactory) {
        FrcKrawlerTheme {
          var requiresMigration by remember { mutableStateOf(migration.requiresMigration()) }
          if (requiresMigration) {
            LegacyMigrationScreen(
              migration = migration,
              onMigrationCompleted = { requiresMigration = false }
            )
          } else {
            Navigation()
          }
        }
      }
    }
  }

  // Provides focus clearing when tapping outside the keyboard to close the keyboard automatically
  override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
    if (event?.action != null && event.action == MotionEvent.ACTION_DOWN) {
      val view = currentFocus
      if (view is ViewGroup) {
        view.clearFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
      }
    }
    return super.dispatchTouchEvent(event)
  }

  override val defaultViewModelProviderFactory: ViewModelProvider.Factory
    get() = viewModelFactory
}