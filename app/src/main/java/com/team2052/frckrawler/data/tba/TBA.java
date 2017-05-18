package com.team2052.frckrawler.data.tba;

import com.team2052.frckrawler.models.Event;

import java.util.List;

import rx.Observable;

/**
 * @author Adam
 */
public class TBA {
    private static String BASE_TBA_API_URL = "http://www.thebluealliance.com/api/v2/";
    public static String EVENT = BASE_TBA_API_URL + "event/%s";
    public static String TEAM = BASE_TBA_API_URL + "team/frc%d";
    private static String EVENT_BY_YEAR = BASE_TBA_API_URL + "events/%d";
    private static String TEAM_EVENTS = BASE_TBA_API_URL + "team/%s/%d/events";

    public static Observable<List<Event>> requestEventsYear(int year) {
        return Observable.just(String.format(TBA.EVENT_BY_YEAR, year))
                .map(HTTP::getResponse)
                .map(HTTP::dataFromResponse)
                .map(JSON::getAsJsonArray)
                .flatMap(Observable::from)
                .map(jsonElement -> JSON.getGson().fromJson(jsonElement, Event.class))
                .toSortedList((event, event2) -> Double.compare(event.getDate().getTime(), event2.getDate().getTime()));
    }
}
