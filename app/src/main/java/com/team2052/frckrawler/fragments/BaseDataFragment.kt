package com.team2052.frckrawler.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import com.team2052.frckrawler.core.data.models.RxDBManager
import com.team2052.frckrawler.di.FragmentComponent
import com.team2052.frckrawler.di.binding.BaseDataBinder
import com.team2052.frckrawler.di.subscribers.BaseDataSubscriber
import com.team2052.frckrawler.interfaces.HasComponent
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Acorp on 11/17/2015.
 * T - Type of the Data
 * V - View Type of Data
 * S - Subscriber Type
 * B - Binder Type
 */
abstract class BaseDataFragment<T, V, S : BaseDataSubscriber<T, V>, B : BaseDataBinder<V>> : Fragment() {
    lateinit var mComponent: FragmentComponent
    lateinit var rxDbManager: RxDBManager

    @Inject lateinit var subscriber: S
    @Inject lateinit var binder: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is HasComponent) {
            mComponent = (activity as HasComponent).component
        }
        inject()
        rxDbManager = mComponent.dbManager()
        subscriber.setConsumer(binder)
        binder.setActivity(activity)
    }

    override fun onResume() {
        super.onResume()
        getObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber)
    }

    abstract fun inject()

    protected abstract fun getObservable(): Observable<out T>

    val data: T
        get() = subscriber.data
}
