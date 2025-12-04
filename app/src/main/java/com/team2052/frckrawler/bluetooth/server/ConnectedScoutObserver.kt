package com.team2052.frckrawler.bluetooth.server

import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.data.model.RemoteScout
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
class ConnectedScoutObserver {
  private val scope = CoroutineScope(Dispatchers.Default)
  private val _devices = MutableStateFlow<Map<String, RemoteScout>>(emptyMap())

  /**
   * Flow containing a list of devices that have performed a sync operation with this server
   */
  val devices: Flow<List<RemoteScout>> = _devices.map { it.values.toList() }

  /**
   * Notify this observer that a scout has synced (or failed to do so)
   */
  internal fun notifyScoutSynced(name: String, address: String, result: OperationResult) {
    scope.launch {
      val deviceMap = _devices.value.toMutableMap()
      deviceMap[address] = RemoteScout(
        name = name,
        address = address,
        lastSyncResult = result
      )
      _devices.emit(deviceMap)
    }
  }
}