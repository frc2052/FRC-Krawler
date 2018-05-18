package com.team2052.frckrawler

import android.arch.lifecycle.AndroidViewModel
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.team2052.frckrawler.core.data.models.RxDBManager
import com.team2052.frckrawler.core.common.v3.JSON
import rx.Observable

fun AndroidViewModel.getDatabase() = RxDBManager.getInstance(getApplication())

fun String.toJsonObject(): JsonObject? = JSON.getAsJsonObject(this)

fun JsonElement.toJsonString() = JSON.getGson().toJson(this)

fun <T> (() -> (T?)).wrapAsObservable(): Observable<T> {
    return Observable.defer({
        val value = invoke() ?: return@defer Observable.empty<T>()
        Observable.just(value)
    })
}

fun PagerAdapter.bindToViewPagerAndTabLayout(viewPager: ViewPager, tabLayout: TabLayout) {
    viewPager.adapter = this
    tabLayout.setupWithViewPager(viewPager)
}
