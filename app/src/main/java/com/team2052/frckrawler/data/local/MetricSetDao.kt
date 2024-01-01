package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricSetDao {
    @Delete
    suspend fun delete(metricSet: MetricSet)

    @Insert
    suspend fun insert(metricSet: MetricSet)

    @Query("SELECT * FROM metric_set WHERE id = :id")
    suspend fun get(id: Int): MetricSet

    @Query("SELECT * FROM metric_set WHERE gameId = :gameId")
    fun getAllForGame(gameId: Int): Flow<List<MetricSet>>

    @Query("SELECT * FROM metric_set")
    fun getAll(): Flow<List<MetricSet>>
}