package com.team2052.frckrawler.bluetooth.server;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.team2052.frckrawler.R;

public class ServerCallbackHandler
{
    public static final int SYNC_ONGOING_ID = 1;
    private Context context;

    public ServerCallbackHandler(Context c)
    {
        context = c.getApplicationContext();
    }

    public void onSyncStart(String deviceName)
    {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_popup_sync);
        b.setContentTitle("Syncing");
        b.setContentText("The FRCKrawler server is syncing with " + deviceName);
        b.setDefaults(0);
        b.setOngoing(true);
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(SYNC_ONGOING_ID, b.build());
    }

    public void onSyncSuccess(String deviceName)
    {
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(SYNC_ONGOING_ID);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setSmallIcon(R.drawable.ic_stat_navigation_accept);
        b.setContentTitle("Synced with " + deviceName);
        b.setContentText("The Server successfully synced with " + deviceName);
        m.notify(0, b.build());
    }

    public void onSyncCancel(String deviceName)
    {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
    }

    public void onSyncError(String deviceName)
    {
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(SYNC_ONGOING_ID);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_dialog_alert);
        b.setContentTitle("Sync error");
        b.setContentText("The FRCKrawler server encountered an error when " + "syncing with " + deviceName);
        m.notify(0, b.build());
    }
}
