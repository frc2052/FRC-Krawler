package com.team2052.frckrawler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.team2052.frckrawler.data.model.Event

@Database(entities = [Event::class], version = 1)
abstract class FRCKrawlerDatabase : RoomDatabase() {
    abstract fun eventDAO(): EventDAO
}