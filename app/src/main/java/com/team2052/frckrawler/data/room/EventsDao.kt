package com.team2052.frckrawler.data.room

import androidx.room.*
import com.team2052.frckrawler.data.model.Event

@Dao
interface EventsDao {
    @Query("SELECT COUNT(*) FROM events")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Event): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: Event)

    @Delete
    suspend fun delete(entity: Event): Int
}
