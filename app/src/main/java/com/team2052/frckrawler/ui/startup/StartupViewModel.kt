package com.team2052.frckrawler.ui.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(

) : ViewModel() {

    fun buffer(onComplete: () -> Unit) {
        viewModelScope.launch {
            delay(2000)
            onComplete()
        }
    }

}