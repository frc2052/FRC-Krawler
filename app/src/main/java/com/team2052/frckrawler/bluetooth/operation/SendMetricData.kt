package com.team2052.frckrawler.bluetooth.operation

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import com.team2052.frckrawler.bluetooth.readResult
import com.team2052.frckrawler.data.local.MetricDatum
import com.team2052.frckrawler.data.local.MetricDatumDao
import com.team2052.frckrawler.data.sync.MetricDataListPacket
import com.team2052.frckrawler.data.sync.toDatumPackets
import kotlinx.coroutines.runBlocking
import okio.BufferedSink
import okio.BufferedSource
import javax.inject.Inject

class SendMetricData @Inject constructor(
    private val metricDatumDao: MetricDatumDao,
    private val moshi: Moshi,
) : SyncOperation {

    @OptIn(ExperimentalStdlibApi::class)
    override fun execute(output: BufferedSink, input: BufferedSource): OperationResult {
        return runBlocking {
            val metrics = metricDatumDao.getRemoteScoutData()
            val packet = MetricDataListPacket(
                metrics = metrics.toDatumPackets()
            )

            val adapter = moshi.adapter<MetricDataListPacket>()
            adapter.toJson(output, packet)

            val result = input.readResult()
            if (result == OperationResult.Success) {
                deleteMetrics(metrics)
            }

            result
        }
    }

    private suspend fun deleteMetrics(metrics: List<MetricDatum>) {
        metricDatumDao.deleteAll(metrics)
    }
}