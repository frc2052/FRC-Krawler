package com.team2052.frckrawler.repository

import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricRecordId
import com.team2052.frckrawler.data.local.transformer.toMetric
import com.team2052.frckrawler.data.local.transformer.toMetricRecord
import com.team2052.frckrawler.data.model.Metric
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricRepository @Inject constructor(
  private val metricDao: MetricDao,
) {

  suspend fun getMetric(metricId: String): Metric {
    return metricDao.getMetric(metricId).toMetric()
  }

  fun getMetrics(metricSetId: Int): Flow<List<Metric>> {
    return metricDao.getMetrics(metricSetId)
      .map { records ->
        records.map { it.toMetric() }
      }
  }

  suspend fun getMetricCount(metricSetId: Int): Int {
    return metricDao.getMetricCount(metricSetId)
  }

  suspend fun isDefaultCommentFieldAtEnd(metricSetId: Int): Boolean {
    val count = getMetricCount(metricSetId)
    val defaultCommentField = metricDao.getDefaultCommentsField(metricSetId)
    return defaultCommentField != null && defaultCommentField.priority == count - 1
  }

  suspend fun moveDefaultCommentToEnd(metricSetId: Int) {
    metricDao.getDefaultCommentsField(metricSetId)?.copy(
      priority = getMetricCount(metricSetId)
    )?.let {
      metricDao.insert(it)
    }
  }

  suspend fun saveMetric(metric: Metric, metricSetId: Int) {
    metricDao.insert(metric.toMetricRecord(metricSetId))
  }

  /**
   * Save a list of metrics and update their priority to match their position in the list
   */
  suspend fun updatePriorities(metrics: List<Metric>) {
    metricDao.updateMetricPriorities(metrics)
  }


  suspend fun deleteMetric(id: String) {
    metricDao.delete(MetricRecordId(id))
  }
}