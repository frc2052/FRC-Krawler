package com.team2052.frckrawler.bluetooth.operation

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import com.team2052.frckrawler.bluetooth.writeResult
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.sync.MetricDataListPacket
import com.team2052.frckrawler.data.sync.toData
import kotlinx.coroutines.runBlocking
import okio.BufferedSink
import okio.BufferedSource
import javax.inject.Inject

class ReceiveMetricData @Inject constructor(
  private val metricDatumDao: MetricDatumDao,
  private val moshi: Moshi,
) : SyncOperation {

  @OptIn(ExperimentalStdlibApi::class)
  override fun execute(output: BufferedSink, input: BufferedSource): OperationResult {
    return runBlocking {
      val adapter = moshi.adapter<MetricDataListPacket>()
      val packet = adapter.fromJson(input)

      packet?.let { persistMetricsToDatabase(packet) }

      output.writeResult(OperationResult.Success)
    }
  }

  private suspend fun persistMetricsToDatabase(packet: MetricDataListPacket) {
    metricDatumDao.insertAll(packet.metrics.toData())
  }
}