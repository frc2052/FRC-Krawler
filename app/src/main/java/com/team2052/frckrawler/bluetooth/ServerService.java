package com.team2052.frckrawler.bluetooth;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;

public class ServerService extends Service {
    public static int SERVER_OPEN_ID = 10;
    public static String EVENT_ID_EXTRA = "com.team2052.frckrawler.bluetooth.eventIDExtra";
    public static String HANDLER_EXTRA = "com.team2052.frckrawler.bluetooth.handlerExtra";
    private DBManager db;
    private ServerThread thread;
    private PendingIntent openServerIntent;

    @Override
    public void onCreate() {
        db = DBManager.getInstance(this);
        thread = null;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;    //Do not allow binding
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (thread == null) {
            //TODO Intent
            NotificationCompat.Builder b = new NotificationCompat.Builder(this);
            b.setSmallIcon(R.drawable.notification_bluetooth);
            b.setContentTitle("Server open");
            b.setContentText("The FRCKrawler server is open for scouts to sync");
            b.setOngoing(true);
            NotificationManager m = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            m.notify(SERVER_OPEN_ID, b.build());
            int eventID = intent.getIntExtra(EVENT_ID_EXTRA, -1);
            Event e = db.getEventsByColumns(new String[]{DBContract.COL_EVENT_ID}, new String[]{Integer.toString(eventID)})[0];
            thread = new ServerThread(this, e, new ServerCallbackHandler(this));
            new Thread(thread).start();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        thread.closeServer();
    }
}
