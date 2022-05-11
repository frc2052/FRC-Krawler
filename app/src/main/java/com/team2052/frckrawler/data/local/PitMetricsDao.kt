package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PitMetricsDao {
    @Delete
    suspend fun delete(singlePitMetric: PitMetric)

    @Insert
    suspend fun insert(singlePitMetric: PitMetric)

    @Query("SELECT * from pitMetric")
    suspend fun getAll(): List<PitMetric>
}