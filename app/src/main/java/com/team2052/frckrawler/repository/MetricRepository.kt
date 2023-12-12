package com.team2052.frckrawler.repository

import com.team2052.frckrawler.data.local.MetricCategory
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

    fun getGameMetrics(
        category: MetricCategory,
        gameId: Int
    ): Flow<List<Metric>> {
        return metricDao.getGameMetricsWithCategory(category, gameId)
            .map { records ->
                records.map { it.toMetric() }
            }
    }

    suspend fun getMetricCountForCategory(
        category: MetricCategory,
        gameId: Int
    ): Int {
        return metricDao.getMetricCountForCategory(category, gameId)
    }

    suspend fun saveMetric(metric: Metric, gameId: Int) {
        metricDao.insert(metric.toMetricRecord(gameId))
    }

    suspend fun deleteMetric(id: Int) {
        metricDao.delete(MetricRecordId(id))
    }
}