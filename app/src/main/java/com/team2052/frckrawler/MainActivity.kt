package com.team2052.frckrawler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.team2052.frckrawler.database.DataBase
import com.team2052.frckrawler.database.entity.Game
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "database-name").build()

        lifecycleScope.launch {
            db.gameDAO().insertOrUpdateGame(Game(0, 2020, "test"));
        }

    }
}