package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MetricsDao {
    @Delete
    suspend fun delete(singleMetric: Metric)

    @Insert
    suspend fun insert(singleMetric: Metric)

    @Query("SELECT * FROM metric")
    suspend fun getAll(): List<Metric>
}