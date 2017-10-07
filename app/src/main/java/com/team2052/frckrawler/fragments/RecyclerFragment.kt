package com.team2052.frckrawler.fragments

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2052.frckrawler.DividerItemDecoration
import com.team2052.frckrawler.R
import io.nlopez.smartadapters.SmartAdapter
import kotlinx.android.synthetic.main.recycler_view.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription

abstract class RecyclerFragment<T : RecyclerFragmentViewModel<*>> : BaseLifeCycleFragment<T>() {
    open val showDividers = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.recycler_view, null, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        list.setHasFixedSize(true)

        val mLayoutManager = LinearLayoutManager(context)
        list.layoutManager = mLayoutManager

        if (showDividers) {
            list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST))
        }

        viewModel.onDataUpdated {
            val creator = SmartAdapter.items(ArrayList<Any>(it))
            provideAdapterCreator(creator)
            creator.into(list)
        }

        lifecycle.addObserver(viewModel)
        super.onViewCreated(view, savedInstanceState)
    }

    abstract fun provideAdapterCreator(creator: SmartAdapter.MultiAdaptersCreator)
}

abstract class RecyclerFragmentViewModel<T>(application: Application) : AndroidViewModel(application), LifecycleObserver {
    val data: BehaviorSubject<List<*>> = BehaviorSubject.create<List<*>>()
    val subscriptions = CompositeSubscription()

    abstract val dataObservable: Observable<List<T>>

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun loadData() {
        dataObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(data::onNext)
    }

    fun onDataUpdated(func: (List<*>) -> Unit) {
        subscriptions.add(data.subscribe(func))
    }

    override fun onCleared() {
        subscriptions.unsubscribe()
    }
}