package com.team2052.frckrawler.data.provider

import androidx.room.*
import com.team2052.frckrawler.data.model.Event

@Dao
interface EventDAO {
    @Query("SELECT COUNT(*) FROM events")
    suspend fun count(): Int

    @Query("SELECT * FROM events WHERE name LIKE :name LIMIT 1")
    suspend fun getEvent(name: String): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Event): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: Event)

    @Delete
    suspend fun delete(entity: Event): Int
}
