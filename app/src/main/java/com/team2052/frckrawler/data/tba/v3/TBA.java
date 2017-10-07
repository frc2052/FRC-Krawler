package com.team2052.frckrawler.data.tba.v3;

import com.team2052.frckrawler.models.Event;

import java.util.List;

import rx.Observable;

/**
 * @author Adam
 */
public class TBA {
    private static String BASE_TBA_API_URL = "http://www.thebluealliance.com/api/v3/";
    public static String EVENT = BASE_TBA_API_URL + "event/%s";
    private static String EVENT_BY_YEAR = BASE_TBA_API_URL + "events/%d";

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
