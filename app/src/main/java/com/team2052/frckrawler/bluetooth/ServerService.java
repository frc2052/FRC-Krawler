package com.team2052.frckrawler.bluetooth;

import android.app.*;
import android.content.*;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Event;

public class ServerService extends Service
{
    public static int SERVER_OPEN_ID = 10;
    public static String EVENT_ID = "EVENT_ID";
    private ServerThread thread;
    private PendingIntent openServerIntent;

    public Intent newInstance(Context context, Event hostedEvent)
    {
        Intent i = new Intent(context, ServerService.class);
        i.putExtra(EVENT_ID, hostedEvent.getId());
        return i;
    }

    @Override
    public void onCreate()
    {
        thread = null;
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;    //Do not allow binding
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (thread == null) {
            Event e = Event.load(Event.class, intent.getLongExtra(EVENT_ID, -1));
            NotificationCompat.Builder b = new NotificationCompat.Builder(this);
            b.setSmallIcon(R.drawable.notification_bluetooth);
            b.setContentTitle("Server open");
            b.setContentText("The FRCKrawler server is open for scouts to sync");
            b.setOngoing(true);
            NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            m.notify(SERVER_OPEN_ID, b.build());
            thread = new ServerThread(this, e, new ServerCallbackHandler(this));
            new Thread(thread).start();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {
        thread.closeServer();
    }
}
