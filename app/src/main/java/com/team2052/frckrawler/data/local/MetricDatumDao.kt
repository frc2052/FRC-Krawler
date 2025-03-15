package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
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

  @Query("SELECT * FROM metricdatum WHERE `group` = 'Match' AND eventId = :eventId")
  suspend fun getEventPitData(eventId: Int): List<MetricDatum>

  @Query("SELECT * FROM metricdatum WHERE `group` = 'Pit' AND eventId = :eventId")
  suspend fun getEventMatchData(eventId: Int): List<MetricDatum>

  @Query("SELECT * FROM metricdatum WHERE `group` = 'Match' AND groupNumber = :matchNumber AND teamNumber = :teamNumber AND eventId = :eventId")
  fun getTeamDatumForMatchMetrics(
    matchNumber: Int,
    teamNumber: String,
    eventId: Int,
  ): Flow<List<MetricDatum>>

  @Query("SELECT * FROM metricdatum WHERE `group` = 'Pit' AND teamNumber = :teamNumber AND eventId = :eventId")
  fun getDatumForPitMetrics(
    teamNumber: String,
    eventId: Int,
  ): Flow<List<MetricDatum>>

  @Query(
    """
        SELECT metricdatum.* FROM metricdatum INNER JOIN metric ON metricId = metric.id
            WHERE metricSetId = ${MetricSet.SCOUT_PIT_METRIC_SET_ID}
                OR metricSetId = ${MetricSet.SCOUT_MATCH_METRIC_SET_ID}
    """
  )
  suspend fun getRemoteScoutData(): List<MetricDatum>

  @Query(
    """
        SELECT metricdatum.* FROM metricdatum INNER JOIN metric ON metricId = metric.id
            WHERE metricSetId = ${MetricSet.SCOUT_PIT_METRIC_SET_ID}
                OR metricSetId = ${MetricSet.SCOUT_MATCH_METRIC_SET_ID}
    """
  )
  fun getRemoteScoutDataFlow(): Flow<List<MetricDatum>>
}
