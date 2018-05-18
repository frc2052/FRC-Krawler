package com.team2052.frckrawler.things;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.team2052.frckrawler.core.bluetooth.server.ServerStatus;
import com.team2052.frckrawler.core.bluetooth.server.ServerThread;

import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class ServerService extends Service {
    private ServerThread mServerThread;
    private Subject<ServerStatus, ServerStatus> serverStatusSubject = BehaviorSubject.create(ServerStatus.Companion.getOff());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mServerThread == null) {
            mServerThread = new ServerThread(serverStatusSubject, this);
            mServerThread.start();
        }
    }
}
