package com.team2052.frckrawler.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.team2052.frckrawler.data.model.Event

// TODO: Depending on future remote databases it could be more organized to move this and the other data source into a "provider" package
@Database(
    entities = [
        Event::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class FRCKrawlerDatabase: RoomDatabase() {
    abstract fun eventsDao(): EventsDao
}