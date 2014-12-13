package com.team2052.frckrawler.events.server;

/**
 * @author Adam
 * @since 12/12/2014.
 */
public class ServerNotificationEvent
{
    private final boolean state;

    public ServerNotificationEvent(boolean state){
        this.state = state;
    }


    public boolean getState()
    {
        return state;
    }
}
