package com.team2052.frckrawler.tba;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class TBA {
    public static String TBA_URL = "http://www.thebluealliance.com/";
    public static String BASE_TBA_API_URL = "http://www.thebluealliance.com/api/v2/";
    public static String EVENT_BY_YEAR = BASE_TBA_API_URL + "events/%d";
    public static String EVENT = BASE_TBA_API_URL + "event/%s";
    public static String TEAM = BASE_TBA_API_URL + "team/frc%d";
    public static String TEAM_EVENTS = BASE_TBA_API_URL + "team/%s/%d/events";

    public Team request_team(long team_number) {
        return JSON.getGson().fromJson(HTTP.dataFromResponse(HTTP.getResponse(String.format(TEAM, team_number))), Team.class);
    }

    public List<Event> request_team_events(long team_number, int year) {
        List<Event> events = new ArrayList<>();
        JsonArray attending_events_json = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(String.format(TEAM_EVENTS, "frc" + team_number, year))));

        for (JsonElement element : attending_events_json) {
            Event event = JSON.getGson().fromJson(element, Event.class);
            events.add(event);
        }

        return events;
    }

}
