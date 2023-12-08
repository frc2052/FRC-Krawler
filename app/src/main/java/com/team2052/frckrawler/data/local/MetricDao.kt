package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MetricDao {
    @Delete
    suspend fun delete(metric: Metric)

    @Insert
    suspend fun insert(metric: Metric)

    @Query("SELECT * FROM metric WHERE category = :category AND gameId = :gameId")
    suspend fun getGameMetricsWithCategory(
        category: MetricCategory,
        gameId: Int
    ): List<Metric>
}
