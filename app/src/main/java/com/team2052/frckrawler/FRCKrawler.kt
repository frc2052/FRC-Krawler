package com.team2052.frckrawler

import android.app.Application

class FRCKrawler : Application() {

    companion object {
        // Change at release
        val release: ReleaseType = ReleaseType.TESTING
    }

    enum class ReleaseType {
        TESTING,
        RELEASE
    }

}