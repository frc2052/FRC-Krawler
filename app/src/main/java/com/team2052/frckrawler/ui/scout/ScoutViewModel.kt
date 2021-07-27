package com.team2052.frckrawler.ui.scout

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.team2052.frckrawler.bluetooth.BluetoothController
import com.team2052.frckrawler.ui.nav.NavScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScoutViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    var currentTab by mutableStateOf(NavScreen.SCOUT_HOME)

}