package com.team2052.frckrawler.events.scout;

import android.support.annotation.Nullable;

import com.team2052.frckrawler.db.Event;

/**
 * @author Adam
 * @since 12/5/2014.
 */
public class NotifyScoutEvent
{
    private final Event event;

    public NotifyScoutEvent(@Nullable Event event)
    {
        this.event = event;
    }

    @Nullable
    public Event getEvent()
    {
        return event;
    }
}
