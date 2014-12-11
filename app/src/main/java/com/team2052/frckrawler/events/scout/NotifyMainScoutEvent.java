package com.team2052.frckrawler.events.scout;

/**
 * @author Adam
 * @since 12/11/2014.
 */
public class NotifyMainScoutEvent
{
    private final String key;

    public NotifyMainScoutEvent(String key){
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }
}
