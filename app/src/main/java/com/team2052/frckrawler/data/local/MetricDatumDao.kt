package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricDatumDao {
    @Delete
    suspend fun delete(datum: MetricDatum)

    @Upsert
    suspend fun insert(datum: MetricDatum)

    @Query("SELECT * FROM metricdatum WHERE `group` = 'Match' AND groupNumber = :matchNumber AND teamNumber = :teamNumber")
    fun getTeamDatumForMatchMetrics(
        matchNumber: Int,
        teamNumber: String
    ): Flow<List<MetricDatum>>

    @Query("SELECT * FROM metricdatum WHERE `group` = 'Pit' AND groupNumber = :matchNumber AND teamNumber = :teamNumber")
    fun getDatumForPitMetrics(
        matchNumber: Int,
        teamNumber: String
    ): Flow<List<MetricDatum>>

}
