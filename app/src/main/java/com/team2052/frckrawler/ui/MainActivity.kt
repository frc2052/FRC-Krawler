package com.team2052.frckrawler.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowInsetsControllerCompat
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.migration.LegacyDatabaseMigration
import com.team2052.frckrawler.ui.migration.LegacyMigrationScreen
import com.team2052.frckrawler.ui.navigation.Navigation
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject lateinit var migration: LegacyDatabaseMigration

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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
      }
    }
    return super.dispatchTouchEvent(event)
  }
}