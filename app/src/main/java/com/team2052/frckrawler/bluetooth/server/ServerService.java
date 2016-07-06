package com.team2052.frckrawler.bluetooth.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.db.Event;

import rx.Observable;

public class ServerService extends Service {
    private static final String TAG = "ServerService";
    private static int SERVER_OPEN_ID = 10;
    private final IBinder mBinder = new ServerServiceBinder();

    ServerThread serverThread = null;

    @Override
    public void onCreate() {
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
        serverThread = new ServerThread(getApplicationContext(), event);
        serverThread.start();
        showNotification();
    }

    /**
     * Shows the notification to the user
     */
    private void showNotification() {
        NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(SERVER_OPEN_ID, makeNotification());
    }

    /**
     * Builds the notification
     */
    private Notification makeNotification() {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setSmallIcon(R.drawable.ic_stat_knightkrawler);
        b.setContentTitle("Server open");
        b.setContentText("The FRC krawler server is open for scouts to sync");
        b.setOngoing(true);
        b.setColor(0x5B0000);

        Intent resultIntent = HomeActivity.newInstance(this, R.id.nav_item_server);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        b.setContentIntent(resultPendingIntent);
        return b.build();
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

    public Observable<ServerStatus> changeServerStatus(Event event, boolean on) {
        return Observable.create(subscriber -> {
            //Event is null, so we can assume we want to turn off the server
            // If the server is on, and we want to turn it off then shut the server down if we want to turn it off
            if (event == null || (isServerOn() && !on)) {
                stopServer();
            } else {
                startServer(event);
            }
            subscriber.onNext(new ServerStatus(event, isServerOn()));
            subscriber.onCompleted();
        });
    }

    public Observable<ServerStatus> getServerStatus() {
        return Observable.create(subscriber -> {
            if (serverThread != null) {
                subscriber.onNext(new ServerStatus(serverThread.getHostedEvent(), isServerOn()));
            } else {
                subscriber.onNext(new ServerStatus(null, isServerOn()));
            }
            subscriber.onCompleted();
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopServer();
        removeNotification();
    }

    public class ServerServiceBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }
}
