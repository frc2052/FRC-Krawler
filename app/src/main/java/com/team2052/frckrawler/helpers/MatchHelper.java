package com.team2052.frckrawler.helpers;

/**
 * @author Adam
 */
public class MatchHelper
{
    public static enum SHORT_TYPES
    {
        QUALS("qm"),
        Q_FINALS("qf"),
        S_FINALS("sf"),
        FINALS("f");

        SHORT_TYPES(String name)
        {
        }
    }

    public static enum LONG_TYPES
    {
        QUALS("Qualification"),
        Q_FINALS("Quarter Finals"),
        S_FINALS("Semi Finals"),
        FINALS("Finals");
        public static final LONG_TYPES[] VALID_TYPES = values();
        public String name;

        LONG_TYPES(String name)
        {
            this.name = name;
        }
    }

}
