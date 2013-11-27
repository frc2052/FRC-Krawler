package com.team2052.frckrawler.bluetooth;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.ServerActivity;

public class ServerCallbackHandler implements SyncCallbackHandler {
	
	public static final int SYNC_ONGOING_ID = 1;
	private Context context;
	private PendingIntent openServerIntent;
	
	private ServerCallbackHandler() {}
	
	public ServerCallbackHandler(Context c) {
		context = c.getApplicationContext();
		Intent resultIntent = new Intent(context, ServerActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(ServerActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		openServerIntent = stackBuilder
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	@Override
	public void onSyncStart(String deviceName) {
		NotificationCompat.Builder b = new NotificationCompat.Builder(context);
		b.setSmallIcon(android.R.drawable.ic_popup_sync);
		b.setContentTitle("Syncing");
		b.setContentText("The FRCKrawler server is syncing with " + deviceName);
		b.setOngoing(true);
		b.setContentIntent(openServerIntent);
		NotificationManager m = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		m.notify(SYNC_ONGOING_ID, b.build());
	}

	@Override
	public void onSyncSuccess(String deviceName) {
		NotificationManager m = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		m.cancel(SYNC_ONGOING_ID);
		NotificationCompat.Builder b = new NotificationCompat.Builder(context);
		b.setSmallIcon(R.drawable.app_icon);
		b.setContentTitle("Synced with " + deviceName);
		b.setContentText("The FRCKrawler server successfully synced with " + deviceName);
		b.setContentIntent(openServerIntent);
		m.notify(0, b.build());
	}

	@Override
	public void onSyncCancel(String deviceName) {
		((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
				.cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
	}

	@Override
	public void onSyncError(String deviceName) {
		NotificationManager m = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		m.cancel(SYNC_ONGOING_ID);
		NotificationCompat.Builder b = new NotificationCompat.Builder(context);
		b.setSmallIcon(android.R.drawable.ic_dialog_alert);
		b.setContentTitle("Sync error");
		b.setContentText("The FRCKrawler server encountered an error when " +
				"syncing with " + deviceName);
		b.setContentIntent(openServerIntent);
		m.notify(0, b.build());
	}
}
