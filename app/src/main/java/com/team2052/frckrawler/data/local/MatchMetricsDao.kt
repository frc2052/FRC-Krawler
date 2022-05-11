package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MatchMetricsDao {
    @Delete
    suspend fun delete(singleMatchMetric: MatchMetric)

    @Insert
    suspend fun insert(singleMatchMetric: MatchMetric)

    @Query("SELECT * FROM matchMetric")
    suspend fun getAll(): List<MatchMetric>
}
