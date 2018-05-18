package com.team2052.frckrawler.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.team2052.frckrawler.core.bluetooth.server.ServerStatus
import com.team2052.frckrawler.core.bluetooth.server.ServerThread
import rx.Observable
import rx.subjects.BehaviorSubject

class ServerService : Service() {
    private val mBinder = ServerServiceBinder()
    private val serverStatusServerStatusSubject = BehaviorSubject.create(ServerStatus.off)
    private var serverThread: ServerThread? = null

    override fun onCreate() {
        serverStatusServerStatusSubject.subscribe(NotificationServerStatusObserver(this))
        super.onCreate()
    }

    override fun onDestroy() {
        stopServer()
        Log.d(TAG, "onDestroy")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private fun startServer() {
        serverThread = ServerThread(serverStatusServerStatusSubject, applicationContext)
        serverThread?.start()
    }

    private fun stopServer() {
        if (isServerOn) {
            serverThread?.closeServer()
            serverThread = null
        }
    }

    private val isServerOn: Boolean get() = serverThread != null

    fun changeServerStatus(on: Boolean) {
        val requestedOff = isServerOn && !on
        if (requestedOff) {
            stopServer()
        } else {
            startServer()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        stopServer()
    }

    fun toObservable(): Observable<ServerStatus> {
        return serverStatusServerStatusSubject
    }

    inner class ServerServiceBinder : Binder() {
        val service: ServerService
            get() = this@ServerService
    }

    companion object {
        private val TAG = "ServerService"
    }
}