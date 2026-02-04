package com.team2052.frckrawler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.team2052.frckrawler.data.model.Metric
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricDao {
  @Delete
  suspend fun delete(metric: MetricRecord)

  @Delete(entity = MetricRecord::class)
  suspend fun delete(recordId: MetricRecordId)

  @Query("DELETE FROM metric WHERE metricSetId = :metricSetId")
  suspend fun deleteAllFromSet(metricSetId: Int)

  @Upsert
  suspend fun insert(metric: MetricRecord)

  @Insert
  suspend fun insertAll(metrics: List<MetricRecord>)

  @Query("UPDATE metric SET priority = :priority WHERE id = :id")
  suspend fun updateMetricPriority(id: String, priority: Int)

  /**
   * Update all metrics in the provided list so their priority matches their index
   * in the list.
   */
  @Transaction
  suspend fun updateMetricPriorities(metrics: List<Metric>) {
    metrics.forEachIndexed { index, metric ->
      updateMetricPriority(metric.id, index)
    }
  }

  @Query("SELECT * FROM metric WHERE id = :id")
  suspend fun getMetric(id: String): MetricRecord?

  @Query("SELECT * FROM metric WHERE metricSetId = :metricSetId ORDER BY priority")
  fun getMetrics(metricSetId: Int): Flow<List<MetricRecord>>

  @Query("SELECT COUNT(id) FROM metric WHERE metricSetId = :metricSetId")
  suspend fun getMetricCount(metricSetId: Int): Int

  @Query("SELECT COUNT(id) FROM metric WHERE metricSetId = :metricSetId")
  fun getMetricCountFlow(metricSetId: Int): Flow<Int>

  @Query("SELECT * FROM metric WHERE name = \"Comments\" AND metricSetId = :metricSetId")
  suspend fun getDefaultCommentsField(metricSetId: Int): MetricRecord?
}
