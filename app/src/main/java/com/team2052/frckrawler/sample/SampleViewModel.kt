package com.team2052.frckrawler.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.team2052.frckrawler.network.ApiManager

// ViewModels hold our screen logic: https://developer.android.com/topic/libraries/architecture/viewmodel
class SampleViewModel : ViewModel() {

  // Our state is a LiveData: https://developer.android.com/topic/libraries/architecture/livedata
  val state: LiveData<SampleScreenState> = liveData {

    // When someone asks for the state for the first time, start with a loading state
    emit(SampleScreenState.Loading)

    // Then load the actual data
    try {
      // The "liveData{}" function supports coroutines (getTeam is a suspend fun)!
      val team = ApiManager.tbaApi.getTeam(2052)
      emit(SampleScreenState.Content(team.name))
    } catch (exception: Exception) {
      // Something went wrong fetching the data from the API, show the error state
      emit(SampleScreenState.Error)
    }
  }

}