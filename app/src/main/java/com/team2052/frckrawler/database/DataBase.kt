package com.team2052.frckrawler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.team2052.frckrawler.database.dao.GamesDAO
import com.team2052.frckrawler.database.entity.Game

private const val DATABASE = "frc_krawler_database"

@Database(entities = [Game::class], version = 1, exportSchema = false)
abstract class DataBase: RoomDatabase() {

    abstract fun gameDAO(): GamesDAO

    companion object {
        // Singleton prevents multiple instances of the database opening at the same time.
        @Volatile
        private var INSTANCE: DataBase? = null

        fun getDatabase(context: Context): DataBase {
            // if the INSTANCE is null, create a new data base, otherwise return it
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, DataBase::class.java, DATABASE).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}