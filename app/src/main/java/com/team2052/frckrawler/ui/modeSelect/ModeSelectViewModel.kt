package com.team2052.frckrawler.ui.modeSelect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.team2052.frckrawler.data.EventRepository
import com.team2052.frckrawler.nbluetooth.BluetoothController

class ModeSelectViewModel(
    //private val eventRepository: EventRepository,
    private val bluetoothController: BluetoothController
) : ViewModel() {

}

class ModeSelectViewModelFactory(
    //private val eventRepository: EventRepository,
    private val bluetoothController: BluetoothController
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ModeSelectViewModel(bluetoothController) as T
    }
}