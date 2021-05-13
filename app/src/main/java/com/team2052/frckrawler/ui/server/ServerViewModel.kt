package com.team2052.frckrawler.ui.server

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ServerViewModel @Inject constructor(

) : ViewModel() {

    val serverState = mutableStateOf(false)

    fun toggleServer() {
        serverState.value = !serverState.value
    }

}