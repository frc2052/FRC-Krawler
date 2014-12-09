package com.team2052.frckrawler.events;

/**
 * @author Adam
 * @since 12/5/2014.
 */
public class NotifyParentEvent
{
    private final String message;

    public NotifyParentEvent(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
