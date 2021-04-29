package com.team2052.frckrawler.ui.modeSelect

import android.util.Log
import androidx.lifecycle.ViewModel

class ModeSelectViewModel : ViewModel() {

    var count = 0

    fun doSomething() {
        count++
        Log.d("VIEW_MODEL_TEST", count.toString())
    }

}