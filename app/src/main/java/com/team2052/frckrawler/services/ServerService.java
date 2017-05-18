package com.team2052.frckrawler.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.team2052.frckrawler.bluetooth.server.ServerStatus;
import com.team2052.frckrawler.bluetooth.server.ServerThread;
import com.team2052.frckrawler.models.Event;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class ServerService extends Service {
    private static final String TAG = "ServerService";
    private static int SERVER_OPEN_ID = 10;
    private final IBinder mBinder = new ServerServiceBinder();
    ServerThread serverThread = null;
    private Subject<ServerStatus, ServerStatus> serverStatusServerStatusSubject = BehaviorSubject.create(ServerStatus.off());

    @Override
    public void onCreate() {
        serverStatusServerStatusSubject.subscribe(new NotificationServerStatusObserver(this));
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        removeNotification();
        stopServer();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void startServer(Event event) {
        serverThread = new ServerThread(serverStatusServerStatusSubject, getApplicationContext(), event);
        serverThread.start();
    }

    /**
     * Removes the notification
     */
    private void removeNotification() {
        NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(SERVER_OPEN_ID);
    }

    private void stopServer() {
        if (isServerOn()) {
            serverThread.closeServer();
            serverThread = null;
        }
        removeNotification();
    }

    private boolean isServerOn() {
        return serverThread != null;
    }

    public void changeServerStatus(Event event, boolean on) {
        boolean requestedOff = (isServerOn() && !on);
        if (event == null || requestedOff) {
            stopServer();
        } else {
            startServer(event);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopServer();
        removeNotification();
    }

    public Observable<ServerStatus> toObservable() {
        return serverStatusServerStatusSubject;
    }

    public class ServerServiceBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }
}