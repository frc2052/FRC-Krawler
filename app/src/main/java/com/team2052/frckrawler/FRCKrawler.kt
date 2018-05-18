package com.team2052.frckrawler

import android.support.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp

import com.team2052.frckrawler.di.ApplicationComponent
import com.team2052.frckrawler.di.DaggerApplicationComponent
import com.team2052.frckrawler.di.FRCKrawlerModule
import com.team2052.frckrawler.di.binding.BinderModule

class FRCKrawler : MultiDexApplication() {
    private var mModule: FRCKrawlerModule? = null
    private var mApplicationComponent: ApplicationComponent? = null
    private var mBinderModule: BinderModule? = null


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    val component: ApplicationComponent
        get() {
            if (mApplicationComponent == null) {
                mApplicationComponent = DaggerApplicationComponent
                        .builder()
                        .build()
            }
            return mApplicationComponent!!
        }

    val module: FRCKrawlerModule
        get() {
            if (mModule == null) {
                mModule = FRCKrawlerModule(this)
            }
            return mModule!!
        }

    val consumerModule: BinderModule
        get() {
            if (mBinderModule == null) {
                mBinderModule = BinderModule()
            }
            return mBinderModule!!
        }
}
