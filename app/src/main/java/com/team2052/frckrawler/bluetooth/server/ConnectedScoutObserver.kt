package com.team2052.frckrawler.bluetooth.server

import com.team2052.frckrawler.data.model.RemoteScout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectedScoutObserver @Inject constructor() {
  private val scope = CoroutineScope(Dispatchers.Default)
  private val _devices = MutableStateFlow<Map<String, RemoteScout>>(emptyMap())

  /**
   * Flow containing a list of devices that have performed a sync operation with this server
   */
  val devices: Flow<List<RemoteScout>> = _devices.map { it.values.toList() }

  /**
   * Notify this observer that a scout has synced
   */
  internal fun notifyScoutSynced(name: String, address: String) {
    scope.launch {
      val deviceMap = _devices.value.toMutableMap()
      val syncedScout = deviceMap[address]
        ?.copy(lastSync = LocalDateTime.now())
        ?: RemoteScout(
          name = name,
          address = address
        )
      deviceMap[address] = syncedScout
      _devices.emit(deviceMap)
    }
  }
}