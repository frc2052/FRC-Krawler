package com.team2052.frckrawler.bluetooth.server;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.team2052.frckrawler.models.Event;

import java.util.List;

@AutoValue
public abstract class ServerStatus {
    private static ServerStatus off = new AutoValue_ServerStatus(null, false, false, null);

    public static ServerStatus off() {
        return off;
    }

    public static ServerStatus create(Event event, boolean state, boolean syncing, BluetoothDevice device) {
        return new AutoValue_ServerStatus(event, state, syncing, device);
    }

    public static ServerStatus create(Event event, boolean state) {
        return new AutoValue_ServerStatus(event, state, false, null);
    }

    @Nullable
    public abstract Event event();

    public abstract boolean state();

    public abstract boolean syncing();

    @Nullable
    public abstract BluetoothDevice device();

    public int findEventIndex(List<Event> eventList) {
        if (event() == null) {
            return 0;
        }

        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getId().equals(event().getId())) {
                return i;
            }
        }
        return 0;
    }
}
