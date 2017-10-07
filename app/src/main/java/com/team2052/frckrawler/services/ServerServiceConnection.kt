package com.team2052.frckrawler.services

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import rx.Observable
import rx.subjects.BehaviorSubject

class ServerServiceConnection : ServiceConnection {
    var valueSubject = BehaviorSubject.create<ServerService>()

    override fun onServiceConnected(p0: ComponentName?, binder: IBinder) {
        valueSubject.onNext((binder as ServerService.ServerServiceBinder).service)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        valueSubject.onNext(null)
    }

    fun toObservable(): Observable<ServerService> = valueSubject
    fun getService(): ServerService? = valueSubject.value
}