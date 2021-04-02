package com.team2052.frckrawler.modeSelect

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class ModeSelectViewModel : ViewModel() {

    val state: LiveData<ModeSelectScreenState> = liveData {
        emit(ModeSelectScreenState.Content)
    }
}