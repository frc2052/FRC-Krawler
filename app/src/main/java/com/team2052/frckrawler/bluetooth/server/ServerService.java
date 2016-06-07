package com.team2052.frckrawler.bluetooth.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.bluetooth.server.events.ServerQuitEvent;
import com.team2052.frckrawler.bluetooth.server.events.ServerStartEvent;
import com.team2052.frckrawler.bluetooth.server.events.ServerStateChangeEvent;
import com.team2052.frckrawler.bluetooth.server.events.ServerStateRequestChangeEvent;
import com.team2052.frckrawler.bluetooth.server.events.ServerStateRequestEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ServerService extends Service {
    public static int SERVER_OPEN_ID = 10;
    public static String TAG = "ServerService";
    private ServerThread thread;

    /**
     * Any part of the application that wants to change the state of the server should use the
     * EventBus and post the ServerStateRequestChangeEvent event to it
     * <p>
     * We carefully time the toggles so the thread doesn't cause any issues when destructing the BluetoothSocket
     */
    @Subscribe
    public void onEvent(ServerStateRequestChangeEvent stateEvent) {
        if (stateEvent.getRequestedState()) {
            if (stateEvent.getEvent() != null && thread == null) {
                //Start the server thread
                thread = new ServerThread(this, stateEvent.getEvent());
                thread.start();
            }
        } else {
            if (thread != null)
                thread.closeServer();
        }
        EventBus.getDefault().post(new ServerStateChangeEvent(thread != null ? thread.getHostedEvent() : null, thread != null && thread.isOpen));
    }

    /**
     * Shows the notification to the user
     */
    public void showNotification() {
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
        b.setContentText("The FRC Krawler server is open for scouts to sync");
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
    public void removeNotification() {
        NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(SERVER_OPEN_ID);
    }

    @Subscribe
    public void onEvent(ServerStateRequestEvent event) {
        EventBus.getDefault().post(new ServerStateChangeEvent(thread != null ? thread.getHostedEvent() : null, thread != null && thread.isOpen));
    }

    @Subscribe
    public void onEvent(ServerQuitEvent event) {
        removeNotification();
        thread = null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    @Subscribe
    public void onEvent(ServerStartEvent event) {
        showNotification();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        thread = null;
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (thread != null)
            thread.closeServer();
        removeNotification();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
