package com.team2052.frckrawler.bluetooth.server;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.team2052.frckrawler.bluetooth.scout.SyncScoutTask;
import com.team2052.frckrawler.db.Event;


public class Server
{
    private static volatile Server instance = null;
    private boolean isOpen;
    private Event event;
    private Context context;
    private BluetoothAdapter adapter;

    private Server(Context c)
    {
        isOpen = false;
        context = c.getApplicationContext();
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static Server getInstance(Context c)
    {
        if (instance == null) synchronized (Server.class) {
            if (instance == null) instance = new Server(c);
        }
        return instance;
    }

    public boolean open(Event hostedEvent)
    {
        if (adapter == null)
            return false;
        if (SyncScoutTask.isTaskRunning())
            return false;
        Intent serverIntent = new Intent(context, ServerService.class);
        serverIntent.putExtra(ServerService.EVENT_ID, hostedEvent.getId());
        context.startService(serverIntent);
        isOpen = true;
        event = hostedEvent;
        return true;
    }

    public boolean close()
    {
        context.stopService(new Intent(context, ServerService.class));
        isOpen = false;
        return true;
    }

    public boolean isOpen()
    {
        return isOpen;
    }

    public Event getHostedEvent()
    {
        return event;
    }
}
