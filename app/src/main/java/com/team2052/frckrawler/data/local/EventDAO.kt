package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.team2052.frckrawler.data.model.Event

@Dao
interface EventDAO {
    @Query("SELECT * FROM event")
    suspend fun getAll(): List<Event>

    @Insert
    suspend fun insertAll(vararg events: Event)
}