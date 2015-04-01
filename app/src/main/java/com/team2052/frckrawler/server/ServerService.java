package com.team2052.frckrawler.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.activities.HomeActivity;
import com.team2052.frckrawler.server.events.ServerStateChangeEvent;

import de.greenrobot.event.EventBus;

public class ServerService extends Service {
    private boolean mServerState = false;

    public static final int MSG_START_SEREVR = 0;
    public static final int MSG_STOP_SEREVR = 1;
    public static final int MSG_STATE = 2;
    private int MSG_GET_RUNNING = 2;

    public static int SERVER_OPEN_ID = 10;
    public static String EVENT_ID = "EVENT_ID";
    public static String EVENT_ID_EXTRA = "com.team2052.ServerService.EVENT_ID.EXTRA";
    private ServerThread thread;
    public static String TAG = "ServerService";

    class ServerIncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_SEREVR:
                    startServer(msg.getData());
                    break;
                case MSG_STOP_SEREVR:
                    stopServer();
                    break;
                case MSG_STATE:
                    EventBus.getDefault().post(new ServerStateChangeEvent(thread != null && thread.isOpen));
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void startServer(Bundle args) {
        Log.d(TAG, "startServer");
        showNotification(makeNotification());
        if (thread != null) {
            thread.closeServer();
        }
        thread = new ServerThread(this, args.getLong(EVENT_ID_EXTRA));
        thread.start();
    }

    private void stopServer() {
        Log.d(TAG, "stopServer");
        removeNotification();
        if (thread != null) {
            thread.closeServer();
        }

        thread = null;
    }

    final Messenger mMessenger = new Messenger(new ServerIncomingHandler());

    @Override
    public void onCreate() {
        thread = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopServer();
        removeNotification();
    }

    private Notification makeNotification() {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setSmallIcon(R.drawable.ic_stat_knightkrawler);
        b.setContentTitle("Server open");
        b.setContentText("The FRCKrawler server is open for scouts to sync");
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

    public void showNotification(Notification notification) {
        NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(SERVER_OPEN_ID, notification);
    }

    public void removeNotification() {
        NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(SERVER_OPEN_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
