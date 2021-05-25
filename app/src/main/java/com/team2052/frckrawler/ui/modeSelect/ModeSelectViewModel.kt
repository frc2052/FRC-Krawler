package com.team2052.frckrawler.ui.modeSelect

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.FRCKrawlerApp
import com.team2052.frckrawler.bluetooth.BluetoothController
import com.team2052.frckrawler.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.Headers
import javax.inject.Inject

@HiltViewModel
class ModeSelectViewModel @Inject constructor(
    private val context: FRCKrawlerApp,
    private val eventRepository: EventRepository,
    val bluetoothController: BluetoothController,
) : ViewModel() {

//    val events: MutableState<List<Event>?> = mutableStateOf(emptyList())
//
//    fun networkTest() {
//        viewModelScope.launch {
////            val result = eventRepository.getEventList(2052)
////            events.value = result
//        }
//    }

    val headers: MutableState<Headers?> = mutableStateOf(null)

    fun headerTest() {
        viewModelScope.launch {
//            eventRepository.getEvents().forEach { event ->
//                headers.value.plus()
//            }

//                .enqueue(
//                object : Callback<List<Event>> {
//                    override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
//                        if (response.isSuccessful) {
//                            Log.d("NETWORK_TEST", "Success!")
//                        }
//                    }
//                    override fun onFailure(call: Call<List<Event>>, t: Throwable) {
//                        Log.d("NETWORK_TEST", "Fail!")
//                    }
//                }
//            )
        }
    }

    // TODO: Implement Data Store
}