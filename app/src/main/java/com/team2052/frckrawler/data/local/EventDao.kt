package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Delete
    suspend fun delete(event: Event)

    @Insert
    suspend fun insert(event: Event)

    @Query("SELECT * FROM event WHERE id = :id")
    suspend fun get(id: Int): Event

    @Query("SELECT * FROM event")
    fun getAll(): Flow<List<Event>>
}