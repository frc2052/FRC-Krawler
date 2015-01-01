package com.team2052.frckrawler.server;

import android.content.Context;

/**
 * @author Adam
 * @since 12/13/2014.
 */
public class ServerEventHandler {
    private static ServerEventHandler instance;
    private final Context context;

    private ServerEventHandler(Context context) {
        this.context = context;
    }

    public static ServerEventHandler getInstance(Context context) {
        if (instance == null) synchronized (ServerEventHandler.class) {
            if (instance == null) instance = new ServerEventHandler(context);
        }
        return instance;
    }
}
