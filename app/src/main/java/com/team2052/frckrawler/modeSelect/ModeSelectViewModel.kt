package com.team2052.frckrawler.modeSelect

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.team2052.frckrawler.network.ApiManager
import com.team2052.frckrawler.sample.SampleScreenState

class ModeSelectViewModel : ViewModel() {

    // Our state is a LiveData: https://developer.android.com/topic/libraries/architecture/livedata
    val state: LiveData<ModeSelectScreenState> = liveData {

        // When someone asks for the state for the first time, start with a loading state
        emit(ModeSelectScreenState.Loading)

        // Then load the actual data
        try {
            // The "liveData{}" function supports coroutines (getTeam is a suspend fun)!
//            val team = ApiManager.tbaApi.getTeam(2052)
//            emit(ModeSelectScreenState.Content(team.name))
        } catch (exception: Exception) {
            // Something went wrong fetching the data from the API, show the error state
            emit(ModeSelectScreenState.Error)
        }

        emit(ModeSelectScreenState.Content)
    }
}