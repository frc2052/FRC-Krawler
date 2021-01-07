package com.team2052.frckrawler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.team2052.frckrawler.database.DataBase
import com.team2052.frckrawler.database.table.Event
import com.team2052.frckrawler.database.table.Game
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "database-name").fallbackToDestructiveMigration().build()

        lifecycleScope.launch {
            db.gameDAO().insert(Game(0, "test", 2020))
            db.eventDAO().insert(Event(0, "event", "12/12/12"))
            val year = db.gameDAO().getGameByYear(2020);

        }
    }
}