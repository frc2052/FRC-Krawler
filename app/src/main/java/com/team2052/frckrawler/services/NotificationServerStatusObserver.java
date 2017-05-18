package com.team2052.frckrawler.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.bluetooth.server.ServerStatus;

import rx.Observer;

public class NotificationServerStatusObserver implements Observer<ServerStatus> {
    public static final int SYNC_ONGOING_ID = 1;
    private static int SERVER_OPEN_ID = 10;
    private final Notification serverOnNotification;
    private final NotificationManager notificationManager;
    private Context context;

    public NotificationServerStatusObserver(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setSmallIcon(R.drawable.ic_stat_knightkrawler);
        b.setContentTitle(context.getResources().getString(R.string.server_open));
        b.setContentText(context.getResources().getString(R.string.server_open_description));
        b.setColor(context.getResources().getColor(R.color.primary));
        b.setOngoing(true);

        Intent resultIntent = HomeActivity.newInstance(context, R.id.nav_item_server);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        b.setContentIntent(resultPendingIntent);

        serverOnNotification = b.build();
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        notificationManager.cancel(SERVER_OPEN_ID);
    }

    @Override
    public void onNext(ServerStatus serverStatus) {
        if (serverStatus.state()) {
            notificationManager.notify(SERVER_OPEN_ID, serverOnNotification);
        } else {
            notificationManager.cancel(SERVER_OPEN_ID);
        }

        if (serverStatus.syncing() && serverStatus.device() != null) {
            notificationManager.notify(SYNC_ONGOING_ID, buildSyncingWithDeviceNotification(serverStatus.device()));
        } else {
            notificationManager.cancel(SYNC_ONGOING_ID);
        }
    }


    private Notification buildSyncingWithDeviceNotification(@NonNull BluetoothDevice device) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setSmallIcon(R.drawable.ic_stat_sync);
        b.setColor(context.getResources().getColor(R.color.primary));
        b.setContentTitle("Syncing");
        b.setContentText("Syncing with " + device.getName());
        b.setDefaults(0);
        b.setProgress(0, 0, true);
        b.setOngoing(true);
        return b.build();
    }
}
