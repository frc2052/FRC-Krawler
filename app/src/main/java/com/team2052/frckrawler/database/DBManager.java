package com.team2052.frckrawler.database;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Match;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.database.models.Robot;

import java.util.List;

/**
 * @author Adam
 */
public class DBManager {
    public static int generateRemoteId(){
        return ((int) (Math.random() * 2147483646));
    }
    public static <T extends Model> List<T> loadAllFromType(Class<T> type) {
        return new Select().from(type).execute();
    }

    public static <T extends Model> List<T> loadAllWhere(Class<T> type, String columnName, Object where) {
        return new Select().from(type).where(columnName + " = ?", where).execute();
    }

    public static <T extends Model> T loadFromTypeAndId(Class<T> type, long id) {
        return Model.load(type, id);
    }

    public static void deleteAllFromType(Class<? extends Model> type){
        new Delete().from(type).execute();
    }

    //MATCHES
    public static List<Match> loadAllMatches() {
        return loadAllFromType(Match.class);
    }

    public static List<Match> loadAllMatchesWhere(String columnName, Object where) {
        return loadAllWhere(Match.class, columnName, where);
    }

    public static From loadFromMatches() {
        return new Select().from(Match.class);
    }

    public static Schedule genenerateSchedule(Event e){
        return new Schedule(e, loadAllMatchesWhere("Event", e.getId()));
    }


    //EVENTS
    public static List<Event> loadAllEvents() {
        return loadAllFromType(Event.class);
    }

    public static From loadFromEvents() {
        return new Select().from(Event.class);
    }

    public static List<Event> loadAllEventsWhere(String columnName, Object where) {
        return loadAllWhere(Event.class, columnName, where);
    }


    //ROBOTS
    public static List<Robot> loadAllRobots() {
        return loadAllFromType(Robot.class);
    }

    public static From loadFromRobots() {
        return new Select().from(Robot.class);
    }

    public static List<Robot> loadAllRobotsWhere(String columnName, Object where) {
        return loadAllWhere(Robot.class, columnName, where);
    }


    //GAMES
    public static List<Game> loadAllGames() {
        return loadAllFromType(Game.class);
    }

    public static From loadFromGames() {
        return new Select().from(Game.class);
    }

    public static List<Game> loadAllGamesWhere(String columnName, Object where) {
        return loadAllWhere(Game.class, columnName, where);
    }


    //METRICS
    public static List<Metric> loadAllMetrics() {
        return loadAllFromType(Metric.class);
    }

    public static From loadFromMetrics() {
        return new Select().from(Metric.class);
    }

    public static List<Metric> loadAllMetricsWhere(String columnName, Object where) {
        return loadAllWhere(Metric.class, columnName, where);
    }


}
