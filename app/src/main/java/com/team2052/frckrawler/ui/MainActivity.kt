package com.team2052.frckrawler.ui

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.navigation.Navigation
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Reset the theme after the splash screen finishes
        setTheme(R.style.Theme_FRCKrawler)

        setContent {
            FrcKrawlerTheme(darkTheme = false) {
                // TODO: Future versions of Jetpack Compose will offer shared element transitions for the nav bar
                Navigation()
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