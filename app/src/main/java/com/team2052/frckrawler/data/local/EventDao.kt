package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
  @Delete
  suspend fun delete(event: Event)

  @Upsert
  suspend fun insert(event: Event): Long

  @Query("SELECT * FROM event WHERE id = :id")
  suspend fun get(id: Int): Event

  @Query("SELECT * FROM event WHERE gameId = :gameId")
  fun getAllForGame(gameId: Int): Flow<List<Event>>

  @Query("SELECT * FROM event")
  fun getAll(): Flow<List<Event>>
}