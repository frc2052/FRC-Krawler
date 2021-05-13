package com.team2052.frckrawler.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.team2052.frckrawler.model.Event
import com.team2052.frckrawler.util.Constants.DATABASE_NAME

@Database(
    entities = [
        Event::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class FRCKrawlerDatabase: RoomDatabase() {

    abstract fun eventDAO(): EventDAO

    // Maybe not necessary
    companion object {
        @Volatile
        private var instance: FRCKrawlerDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createFRCKrawlerDatabase(context)
        }

        private fun createFRCKrawlerDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                FRCKrawlerDatabase::class.java,
                DATABASE_NAME
            )
    }

}