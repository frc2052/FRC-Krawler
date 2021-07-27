package com.team2052.frckrawler.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.team2052.frckrawler.ui.nav.NavGraph
import com.team2052.frckrawler.ui.nav.NavScreen
import com.team2052.frckrawler.ui.theme.FRCKrawlerColor
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Error handle if the request is denied
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 0)
            }
        }

        setContent {
            FrcKrawlerTheme(darkTheme = false) {
                NavGraph()
            }
        }
    }

    // TODO: This could cause problems in the future and may only be a temporary solution
    // Provides focus clearing when tapping outside the keyboard
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