package com.team2052.frckrawler.data.export

import android.content.Context
import android.net.Uri
import com.team2052.frckrawler.data.export.aggregator.MetricDataAggregator
import com.team2052.frckrawler.data.export.aggregator.RawMetricDataAggregator
import com.team2052.frckrawler.data.export.aggregator.SummaryMetricDataAggregator
import com.team2052.frckrawler.data.export.converter.CsvRowConverter
import com.team2052.frckrawler.data.export.converter.RawMetricsCsvRowConverter
import com.team2052.frckrawler.data.export.converter.SummaryMetricsCsvRowConverter
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.local.prefs.FrcKrawlerPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.BufferedSink
import okio.buffer
import okio.sink
import javax.inject.Inject

class OkioDataExporter @Inject constructor(
  private val metricDatumDao: MetricDatumDao,
  private val teamAtEventDao: TeamAtEventDao,
  private val metricDao: MetricDao,
  private val prefs: FrcKrawlerPreferences,
  @ApplicationContext private val context: Context,
) : DataExporter {
  override suspend fun export(fileUri: Uri, type: ExportType, eventId: Int) {
    withContext(Dispatchers.IO + CoroutineName("OkioDataExporter")) {
      val includeTeamNames = prefs.exportIncludeTeamNames.first()
      val includeMatchMetrics = prefs.exportIncludeMatchMetrics.first()
      val includePitMetrics = prefs.exportIncludePitMetrics.first()

      val data = getMetricData(includeMatchMetrics, includePitMetrics, eventId)
      val metricsAsync = async {
        data.map { it.metricId }
          .distinct()
          .map { metricDao.getMetric(it) }
      }
      val teamsAsync = async {
        teamAtEventDao.getAllTeams(eventId)
          .first()
          .associateBy { it.number }
      }

      val metrics = metricsAsync.await()
      val teams = teamsAsync.await()

      // TODO return error
      val sink = context.contentResolver.openOutputStream(fileUri)?.sink() ?: return@withContext
      val writer = sink.buffer()

      when (type) {
        ExportType.Summary -> {
          val aggregator = SummaryMetricDataAggregator(teams)
          val converter = SummaryMetricsCsvRowConverter(includeTeamNames)
          writer.writeCsv(aggregator, converter, metrics, data)
        }
        ExportType.Raw -> {
          val aggregator = RawMetricDataAggregator(teams)
          val converter = RawMetricsCsvRowConverter(includeTeamNames)
          writer.writeCsv(aggregator, converter, metrics, data)
        }
      }

      writer.close()
      sink.close()
    }
  }

  private fun <T> BufferedSink.writeCsv(
    aggregator: MetricDataAggregator<T>,
    converter: CsvRowConverter<T>,
    metrics: List<MetricRecord>,
    data: List<MetricDatum>,
  ) {
    val aggregatedRows = aggregator.aggregate(
      metrics = metrics,
      data = data
    )

    writeUtf8(converter.getHeader(metrics))
    aggregatedRows.forEach { row ->
      writeUtf8(converter.getDataRow(row))
    }
  }

  private suspend fun getMetricData(
    includeMatchMetrics: Boolean,
    includePitMetrics: Boolean,
    eventId: Int
  ): List<MetricDatum> {
    val matchData = if (includeMatchMetrics) {
      metricDatumDao.getEventMatchData(eventId)
    } else emptyList()

    val pitData = if (includePitMetrics) {
      metricDatumDao.getEventPitData(eventId)
    } else emptyList()

    return matchData + pitData
  }
}