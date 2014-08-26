package com.team2052.frckrawler.bluetooth;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.team2052.frckrawler.R;

public class ServerCallbackHandler implements SyncCallbackHandler {
    public static final int SYNC_ONGOING_ID = 1;
    private Context context;

    //TODO INTENT
    public ServerCallbackHandler(Context c) {
        context = c.getApplicationContext();
    }

    @Override
    public void onSyncStart(String deviceName) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_popup_sync);
        b.setContentTitle("Syncing");
        b.setContentText("The FRCKrawler server is syncing with " + deviceName);
        b.setDefaults(0);
        b.setOngoing(true);
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(SYNC_ONGOING_ID, b.build());
    }

    @Override
    public void onSyncSuccess(String deviceName) {
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(SYNC_ONGOING_ID);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setSmallIcon(R.drawable.splash_logo);
        b.setContentTitle("Synced with " + deviceName);
        b.setContentText("The FRCKrawler server successfully synced with " + deviceName);
        m.notify(0, b.build());
    }

    @Override
    public void onSyncCancel(String deviceName) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
    }

    @Override
    public void onSyncError(String deviceName) {
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(SYNC_ONGOING_ID);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_dialog_alert);
        b.setContentTitle("Sync error");
        b.setContentText("The FRCKrawler server encountered an error when " + "syncing with " + deviceName);
        m.notify(0, b.build());
    }
}
