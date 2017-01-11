package com.team2052.frckrawler.database;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.comparators.MatchNumberComparator;
import com.team2052.frckrawler.comparators.RobotTeamNumberComparator;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.metric.MetricTypeEntry;
import com.team2052.frckrawler.metric.MetricTypeEntryHandler;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.MetricHelper;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import rx.Observable;

@Singleton
/**
 * RX Specific Functions for DB
 * If you want table specific functions with no rx, add them to the respective tables
 */
public class RxDBManager extends DBManager {
    private static RxDBManager instance;

    protected RxDBManager(Context context) {
        super(context);
    }

    public static synchronized RxDBManager getInstance(Context context) {
        if (instance == null) {
            instance = new RxDBManager(context);
        }
        return instance;
    }

    public void runInTx(Runnable runnable) {
        daoSession.runInTx(runnable);
    }

    public void deleteAll() {
    }

    public Observable<Map<String, String>> teamInfo(long team_id) {
        return getTeamsTable().getDao().rx().load(team_id).map(team -> {
            Map<String, String> info = Maps.newLinkedHashMap();
            info.put("Nickname", team.getName());

            JsonObject data = JSON.getAsJsonObject(team.getData());

            if (data.has("rookie_year") && !data.get("rookie_year").isJsonNull()) {
                info.put("Rookie Year", data.get("rookie_year").getAsString());
            }

            if (data.has("long_name") && !data.get("long_name").isJsonNull()) {
                info.put("Name", data.get("long_name").getAsString());
            }
            return info;
        });
    }

    public Observable<List<Event>> eventsByGame(long game_id) {
        return getEventsTable().query(null, game_id).rx().list();
    }

    public Observable<List<Event>> allEvents() {
        return getEventsTable().query(null, null).rx().list();
    }

    public Observable<List<Event>> robotAtEvents(long robot_id) {
        return getRobotsTable()
                .query(robot_id, null, null)
                .rx()
                .unique()
                .concatMap(robot -> {
                    robot.resetRobotEventList();
                    return Observable.from(robot.getRobotEventList());
                })
                .map(RobotEvent::getEvent)
                .toList();
    }

    public Observable<List<Robot>> robotsWithTeam(long team_id) {
        return getRobotsTable().query(null, team_id, null).rx().list();
    }

    public Observable<List<Robot>> robotsAtEvent(long event_id) {
        return getEventsTable().getDao().rx().load(event_id).map(event -> getEventsTable().getRobotEvents(event)).map(robotEvents -> {
            List<Robot> robots = Lists.newArrayListWithCapacity(robotEvents.size());
            for (int i = 0; i < robotEvents.size(); i++) {
                robots.add(robotEvents.get(i).getRobot());
            }
            Collections.sort(robots, new RobotTeamNumberComparator());
            return robots;
        });
    }

    public Observable<List<Game>> allGames() {
        return getGamesTable().getDao().rx().loadAll();
    }

    public Observable<List<Metric>> metricsInGame(long game_id, @Nullable Integer category) {
        QueryBuilder<Metric> query = getMetricsTable().getQueryBuilder().where(MetricDao.Properties.Game_id.eq(game_id));
        if (category != null)
            query.where(MetricDao.Properties.Category.eq(category));
        return query.rx().list();
    }

    public Observable<List<Team>> allTeams() {
        return getTeamsTable().getDao().rx().loadAll();
    }

    public Observable<List<Match>> matchesAtEvent(long event_id) {
        QueryBuilder<Match> query = getMatchesTable().getQueryBuilder().where(MatchDao.Properties.Event_id.eq(event_id));

        return query.rx().list()
                .map(matches -> {
                    Collections.sort(matches, new MatchNumberComparator());
                    return matches;
                });
    }

    public Observable<? extends Map<String, String>> gameInfo(Game mGame) {
        return Observable.just(mGame).map(game -> {
            Map<String, String> info = Maps.newLinkedHashMap();
            mGame.resetEventList();
            mGame.resetRobotList();
            Resources resources = context.getResources();
            info.put(resources.getString(R.string.game_info_num_of_event), Integer.toString(mGame.getEventList().size()));
            info.put(resources.getString(R.string.game_info_num_of_robots), Integer.toString(mGame.getRobotList().size()));
            info.put(resources.getString(R.string.game_info_num_of_match_metrics), Integer.toString(getMetricsTable().getNumberOfMetrics(mGame, MetricHelper.MATCH_PERF_METRICS)));
            info.put(resources.getString(R.string.game_info_num_of_pit_metrics), Integer.toString(getMetricsTable().getNumberOfMetrics(mGame, MetricHelper.ROBOT_METRICS)));
            return info;
        });
    }

    public Observable<? extends Map<String, String>> eventInfo(Event event_) {
        return Observable.just(event_).map(event -> {
            Map<String, String> info = Maps.newLinkedHashMap();
            Resources resources = context.getResources();
            info.put(resources.getString(R.string.event_info_num_of_teams), Integer.toString(getEventsTable().getTeamsAtEvent(event).size()));
            info.put(resources.getString(R.string.event_info_num_of_matches), Integer.toString(getEventsTable().getMatches(event).size()));
            info.put(resources.getString(R.string.event_info_num_of_pit_data), Integer.toString(getEventsTable().getPitData(event).size()));
            info.put(resources.getString(R.string.event_info_num_of_match_data), Integer.toString(getEventsTable().getMatchData(event).size()));
            return info;
        });
    }

    public Observable<? extends Map<String, String>> metricInfo(long metricId) {
        return Observable.just(metricId)
                .map(id -> getMetricsTable().load(id))
                .map(metric -> {
                    Map<String, String> info = Maps.newLinkedHashMap();
                    info.put("Enabled", CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, String.valueOf(metric.getEnabled())));
                    MetricTypeEntry<?> typeEntry = MetricTypeEntryHandler.INSTANCE.getTypeEntry(metric.getType());
                    if (typeEntry != null) {
                        typeEntry.addInfo(metric, info);
                    }
                    return info;
                });
    }
}
