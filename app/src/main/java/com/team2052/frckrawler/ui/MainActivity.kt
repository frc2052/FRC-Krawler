package com.team2052.frckrawler.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.nav.Navigation
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceLarge
import com.team2052.frckrawler.ui.theme.spaceMedium
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val LOCATION_REQUEST_CODE = 0

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /**
     * 21 - Automatically Granted
     * 22 - Automatically Granted
     * 23 - Coarse Location Needed
     * 24 - Coarse Location Needed
     * 25 - Coarse Location Needed
     * 26 - Coarse Location Needed
     * 27 - Use Companion Device Pairing
     * 28 - Use Companion Device Pairing
     * 29 - Use Companion Device Pairing
     * 30 - Use Companion Device Pairing
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apps installed on Android version 5.1 (API level 22) and lower don't need permission
        // to access dangerous permissions like location
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

        }

        // Reset the theme after the splash screen finishes
        setTheme(R.style.Theme_FRCKrawler)

        setContent {
            FrcKrawlerTheme(darkTheme = false) {
                // TODO: Future versions of Jetpack Compose will offer shared element transitions for the nav bar
                Navigation()
            }
        }
    }

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