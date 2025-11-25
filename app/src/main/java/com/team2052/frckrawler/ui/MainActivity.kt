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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.migration.LegacyDatabaseMigration
import com.team2052.frckrawler.di.ActivityKey
import com.team2052.frckrawler.ui.migration.LegacyMigrationScreen
import com.team2052.frckrawler.ui.navigation.Navigation
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(
  private val viewModelFactory: ViewModelProvider.Factory,
  private val migration: LegacyDatabaseMigration,
) : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Reset the theme after the splash screen finishes
    setTheme(R.style.Theme_FRCKrawler)

    enableEdgeToEdge(
      navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
    )

    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

    setContent {
      FrcKrawlerTheme {
        var requiresMigration by remember { mutableStateOf(migration.requiresMigration()) }
        if (requiresMigration) {
          LegacyMigrationScreen(
            migration = migration,
            onMigrationCompleted =  { requiresMigration = false }
          )
        } else {
          Navigation()
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