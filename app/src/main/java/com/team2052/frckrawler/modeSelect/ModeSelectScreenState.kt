package com.team2052.frckrawler.modeSelect

sealed class ModeSelectScreenState {
    object Loading: ModeSelectScreenState()
    object Error: ModeSelectScreenState()
    object Content: ModeSelectScreenState()
}