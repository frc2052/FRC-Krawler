package com.team2052.frckrawler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.team2052.frckrawler.network.ApiManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Temporary to test API calls
        lifecycleScope.launch {
            val team = ApiManager.tbaApi.getTeam(2052)
            Log.d("NetworkingTest", team.toString())
        }
    }
}