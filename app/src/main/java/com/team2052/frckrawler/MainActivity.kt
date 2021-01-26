package com.team2052.frckrawler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.team2052.frckrawler.network.ApiManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Temporary to test API calls
        lifecycleScope.launch {
            val match = ApiManager.tbaApi.getMatches( 2052, "2019mndu2")
            Log.d("NetworkingTest", match.toString())
        }
    }
}