package com.team2052.frckrawler.bluetooth.server

import android.bluetooth.BluetoothDevice

import com.team2052.frckrawler.models.Event

data class ServerStatus(val event: Event? = null, val state: Boolean = false, val syncing: Boolean = false, val device: BluetoothDevice? = null) {
    fun findEventIndex(eventList: List<Event>): Int {
        if (event == null) {
            return 0
        }

        return eventList.indices.firstOrNull { eventList[it].id == event.id }
                ?: 0
    }

    companion object {
        val off = ServerStatus()
    }
}
