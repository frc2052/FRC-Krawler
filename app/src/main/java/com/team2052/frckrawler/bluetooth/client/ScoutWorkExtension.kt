package com.team2052.frckrawler.bluetooth.client

import androidx.work.Data
import com.team2052.frckrawler.bluetooth.OperationResult

fun Data.getFailureReason(): OperationResult? {
    if (!this.keyValueMap.containsKey(ScoutSyncWorker.RESULT_FAILURE_CODE)) {
        return null
    }
    return OperationResult.parse(getInt(ScoutSyncWorker.RESULT_FAILURE_CODE, OperationResult.Unknown.id))
}