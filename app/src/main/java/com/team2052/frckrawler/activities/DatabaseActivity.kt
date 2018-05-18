package com.team2052.frckrawler.activities

import android.os.Bundle
import com.team2052.frckrawler.FRCKrawler
import com.team2052.frckrawler.core.data.models.RxDBManager
import com.team2052.frckrawler.di.DaggerFragmentComponent
import com.team2052.frckrawler.di.FragmentComponent
import com.team2052.frckrawler.di.subscribers.SubscriberModule
import com.team2052.frckrawler.interfaces.HasComponent
import javax.inject.Inject

abstract class DatabaseActivity : NavigationDrawerActivity(), HasComponent {
    internal var mComponent: FragmentComponent? = null
    @Inject
    lateinit var rxDbManager: RxDBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun getComponent(): FragmentComponent {
        if (mComponent == null) {
            val app = application as FRCKrawler
            mComponent = DaggerFragmentComponent
                    .builder()
                    .fRCKrawlerModule(app.module)
                    .subscriberModule(SubscriberModule(this))
                    .applicationComponent(app.component)
                    .build()
        }
        return mComponent!!
    }

    abstract fun inject()

    override fun onNavDrawerOpened() {

    }

    override fun onNavDrawerClosed() {

    }

    companion object {
        val PARENT_ID = "PARENT_ID"
    }
}
