package com.team2052.frckrawler.bluetooth.operation

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import com.team2052.frckrawler.bluetooth.writeResult
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.sync.MetricDataListPacket
import com.team2052.frckrawler.data.sync.toData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import okio.BufferedSink
import okio.BufferedSource

@AssistedFactory
interface ReceiveMetricDataFactory {
  fun create(args: ReceiveMetricDataArgs): ReceiveMetricData
}

data class ReceiveMetricDataArgs(
  val eventId: Int,
)

class ReceiveMetricData @AssistedInject constructor(
  @Assisted private val args: ReceiveMetricDataArgs,

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
    metricDatumDao.insertAll(packet.metrics.toData(args.eventId))
  }
}