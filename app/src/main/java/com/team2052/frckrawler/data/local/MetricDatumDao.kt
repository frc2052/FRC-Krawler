package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.team2052.frckrawler.data.local.view.RemoteScoutMetrics
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricDatumDao {
    @Delete
    suspend fun delete(datum: MetricDatum)

    @Delete
    suspend fun deleteAll(data: List<MetricDatum>)

    @Upsert
    suspend fun insert(datum: MetricDatum)

    @Insert
    suspend fun insertAll(data: List<MetricDatum>)

    @Query("SELECT * FROM metricdatum WHERE `group` = 'Match' AND groupNumber = :matchNumber AND teamNumber = :teamNumber")
    fun getTeamDatumForMatchMetrics(
        matchNumber: Int,
        teamNumber: String
    ): Flow<List<MetricDatum>>

    @Query("SELECT * FROM metricdatum WHERE `group` = 'Pit' AND teamNumber = :teamNumber")
    fun getDatumForPitMetrics(
        teamNumber: String
    ): Flow<List<MetricDatum>>

    @Query("SELECT * FROM remotescoutmetrics")
    suspend fun getRemoteScoutData(): RemoteScoutMetrics
}
