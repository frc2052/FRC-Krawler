package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricDao {
    @Delete
    suspend fun delete(metric: MetricRecord)

    @Delete(entity = MetricRecord::class)
    suspend fun delete(recordId: MetricRecordId)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metric: MetricRecord)

    @Query("SELECT * FROM metric WHERE category = :category AND gameId = :gameId")
    fun getGameMetricsWithCategory(
        category: MetricCategory,
        gameId: Int
    ): Flow<List<MetricRecord>>

    @Query("SELECT COUNT(id) FROM metric WHERE category = :category AND gameId = :gameId")
    suspend fun getMetricCountForCategory(
        category: MetricCategory,
        gameId: Int
    ): Int
}
