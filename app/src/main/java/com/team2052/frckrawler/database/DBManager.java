package com.team2052.frckrawler.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.db.DaoMaster;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.GameDao;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.TeamDao;
import com.team2052.frckrawler.db.User;
import com.team2052.frckrawler.db.UserDao;
import com.team2052.frckrawler.tba.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Used to keep a clean database
 * There is no CASCADE ON DELETE function with greenDAO, so we have to configure it manually.
 *
 * @author Adam
 * @since 10/7/2014
 */
public class DBManager {
    private static DBManager instance;
    private final Games mGames;
    private final Events mEvents;
    private final Robots mRobots;
    private final Metrics mMetrics;
    private final RobotEvents mRobotEvents;
    private final MatchComments mMatchComments;
    private final MatchDatas mMatchDatas;
    private final PitDatas mPitDatas;
    private final Matches mMatches;
    private final Teams mTeams;
    private final Users mUsers;

    private final MatchDataDao matchDataDao;
    private final PitDataDao pitDataDao;
    private final GameDao gameDao;
    private final MatchCommentDao matchCommentDao;
    private final RobotEventDao robotEventDao;
    private final MatchDao matchDao;
    private final MetricDao metricDao;
    private final UserDao userDao;
    private final RobotDao robotDao;
    private final DaoMaster daoMaster;
    private final EventDao eventDao;

    private Context context;
    private DaoSession daoSession;

    private TeamDao teamDao;

    private DBManager(Context context) {
        this.context = context;

        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, "frc-krawler-database", null);

        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        //DAO's
        matchDataDao = daoSession.getMatchDataDao();
        pitDataDao = daoSession.getPitDataDao();
        matchCommentDao = daoSession.getMatchCommentDao();
        robotEventDao = daoSession.getRobotEventDao();
        matchDao = daoSession.getMatchDao();
        metricDao = daoSession.getMetricDao();
        teamDao = daoSession.getTeamDao();
        userDao = daoSession.getUserDao();
        gameDao = daoSession.getGameDao();
        eventDao = daoSession.getEventDao();
        robotDao = daoSession.getRobotDao();

        mGames = new Games();
        mEvents = new Events();
        mRobots = new Robots();
        mMetrics = new Metrics();
        mRobotEvents = new RobotEvents();
        mMatchComments = new MatchComments();
        mMatchDatas = new MatchDatas();
        mPitDatas = new PitDatas();
        mMatches = new Matches();
        mTeams = new Teams();
        mUsers = new Users();
    }

    public static synchronized DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
        return instance;
    }

    public void runInTx(Runnable runnable) {
        daoSession.runInTx(runnable);
    }

    public void deleteAll() {
        matchDataDao.deleteAll();
        pitDataDao.deleteAll();
        gameDao.deleteAll();
        matchCommentDao.deleteAll();
        robotEventDao.deleteAll();
        matchDao.deleteAll();
        metricDao.deleteAll();
        userDao.deleteAll();
        robotDao.deleteAll();
        eventDao.deleteAll();
    }

    public Games getGamesTable() {
        return mGames;
    }

    public Events getEventsTable() {
        return mEvents;
    }

    public Robots getRobotsTable() {
        return mRobots;
    }

    public Metrics getMetricsTable() {
        return mMetrics;
    }

    public RobotEvents getRobotEvents() {
        return mRobotEvents;
    }

    public MatchComments getMatchComments() {
        return mMatchComments;
    }

    public MatchDatas getMatchDataTable() {
        return mMatchDatas;
    }

    public PitDatas getPitDataTable() {
        return mPitDatas;
    }

    public Matches getMatchesTable() {
        return mMatches;
    }

    public Teams getTeamsTable() {
        return mTeams;
    }

    public Users getUsersTable() {
        return mUsers;
    }

    private interface Table<T> {
        T load(long id);

        void delete(T model);

        void delete(List<T> models);

        QueryBuilder<T> getQueryBuilder();
    }

    public class Games implements Table<Game> {
        @Override
        public Game load(long id) {
            return gameDao.load(id);
        }

        public List<Robot> getRobots(Game game) {
            game.resetRobotList();
            return game.getRobotList();
        }

        public List<Event> getEvents(Game game) {
            game.resetEventList();
            return game.getEventList();
        }

        public List<Metric> getMetrics(Game game) {
            game.resetMetricList();
            return game.getMetricList();
        }

        public void insert(Game game) {
            gameDao.insertOrReplace(game);
        }

        public List<Game> loadAll() {
            return gameDao.loadAll();
        }

        @Override
        public void delete(List<Game> games) {
            for (Game game : games) {
                delete(game);
            }
        }


        @Override
        public QueryBuilder<Game> getQueryBuilder() {
            return gameDao.queryBuilder();
        }

        @Override
        public void delete(Game game) {
            game.resetEventList();
            game.resetRobotList();
            game.resetMetricList();
            DBManager.this.getEventsTable().delete(game.getEventList());
            DBManager.this.getRobotsTable().delete(game.getRobotList());
            gameDao.delete(game);
        }


    }

    public class Events implements Table<Event> {

        public List<Event> loadAll() {
            return eventDao.loadAll();
        }

        public List<Robot> getRobots(Event event) {
            List<Robot> robots = new ArrayList<>();
            for (RobotEvent robotEvent : getRobotEvents(event)) {
                robots.add(DBManager.this.getRobotEvents().getRobot(robotEvent));
            }
            return robots;
        }

        public List<RobotEvent> getRobotEvents(Event event) {
            event.resetRobotEventList();
            return event.getRobotEventList();
        }

        public Game getGame(Event event) {
            return getGamesTable().load(event.getGame_id());
        }

        public List<Team> getTeamsAtEvent(Event event) {
            List<RobotEvent> robotEventList = event.getRobotEventList();
            List<Team> teams = Lists.newArrayList();
            for (RobotEvent robotEvent : robotEventList) {
                teams.add(DBManager.this.getRobotEvents().getTeam(robotEvent));
            }
            Collections.sort(teams, (lhs, rhs) -> Double.compare(lhs.getNumber(), rhs.getNumber()));
            return teams;
        }

        @Override
        public Event load(long id) {
            return eventDao.load(id);
        }

        public void insert(Event event) {
            eventDao.insertOrReplace(event);
        }

        public QueryBuilder<Event> query(String fms_id, Long game_id) {
            QueryBuilder<Event> queryBuilder = getQueryBuilder();
            if (fms_id != null)
                queryBuilder.where(EventDao.Properties.Fmsid.eq(fms_id));
            if (game_id != null)
                queryBuilder.where(EventDao.Properties.Game_id.eq(game_id));
            return queryBuilder;
        }

        public List<Match> getMatches(Event event) {
            event.resetMatchList();
            return event.getMatchList();
        }

        public List<MatchData> getMatchData(Event event) {
            event.resetMatchDataList();
            return event.getMatchDataList();
        }

        public List<PitData> getPitData(Event event) {
            event.resetPitDataList();
            return event.getPitDataList();
        }

        public List<MatchComment> getMatchComments(Event event) {
            event.resetMatchCommentList();
            return event.getMatchCommentList();
        }


        @Override
        public void delete(List<Event> events) {
            for (Event event : events) {
                delete(event);
            }
        }

        @Override
        public QueryBuilder<Event> getQueryBuilder() {
            return eventDao.queryBuilder();
        }

        @Override
        public void delete(Event model) {
            getMatchDataTable().delete(getMatchData(model));
            getPitDataTable().delete(getPitData(model));
            DBManager.this.getMatchComments().delete(getMatchComments(model));
            getMatchesTable().delete(getMatches(model));
            DBManager.this.getRobotEvents().delete(getRobotEvents(model));
            eventDao.delete(model);
        }
    }

    public class MatchComments implements Table<MatchComment> {
        public boolean insertMatchComment(MatchComment matchComment) {
            matchComment.setId(null);
            QueryBuilder<MatchComment> matchCommentQueryBuilder = matchCommentDao.queryBuilder();
            matchCommentQueryBuilder.where(MatchCommentDao.Properties.Event_id.eq(matchComment.getEvent_id()));
            matchCommentQueryBuilder.where(MatchCommentDao.Properties.Robot_id.eq(matchComment.getRobot_id()));
            matchCommentQueryBuilder.where(MatchCommentDao.Properties.Match_number.eq(matchComment.getMatch_number()));
            matchCommentQueryBuilder.where(MatchCommentDao.Properties.Match_type.eq(matchComment.getMatch_type()));
            long count = matchCommentQueryBuilder.count();
            if (count > 0) {
                MatchComment currentData = matchCommentQueryBuilder.unique();
                currentData.setComment(matchComment.getComment());
                matchCommentDao.update(currentData);
                return false;
            } else {
                matchCommentDao.insert(matchComment);
                return true;
            }
        }

        public QueryBuilder<MatchComment> query(Integer match_number, Integer game_type, Long robot_id, Long event_id) {
            QueryBuilder<MatchComment> queryBuilder = getQueryBuilder();
            if (match_number != null)
                queryBuilder.where(MatchCommentDao.Properties.Match_number.eq(match_number));
            if (game_type != null)
                queryBuilder.where(MatchCommentDao.Properties.Match_type.eq(game_type));
            if (robot_id != null)
                queryBuilder.where(MatchCommentDao.Properties.Robot_id.eq(robot_id));
            if (event_id != null)
                queryBuilder.where(MatchCommentDao.Properties.Event_id.eq(event_id));
            return queryBuilder;
        }

        public List<MatchComment> loadAll() {
            return matchCommentDao.loadAll();
        }

        @Override
        public MatchComment load(long id) {
            return matchCommentDao.load(id);
        }


        @Override
        public void delete(MatchComment matchComment) {
            matchCommentDao.delete(matchComment);
        }

        @Override
        public void delete(List<MatchComment> models) {
            for (MatchComment model : models) {
                delete(model);
            }
        }

        @Override
        public QueryBuilder<MatchComment> getQueryBuilder() {
            return matchCommentDao.queryBuilder();
        }
    }

    public class PitDatas implements Table<PitData> {
        public boolean insert(PitData pitData) {
            pitData.setId(null);
            QueryBuilder<PitData> pitDataQueryBuilder = pitDataDao.queryBuilder();
            pitDataQueryBuilder.where(PitDataDao.Properties.Robot_id.eq(pitData.getRobot_id()));
            pitDataQueryBuilder.where(PitDataDao.Properties.Metric_id.eq(pitData.getMetric_id()));
            long count = pitDataQueryBuilder.count();

            if (count > 0) {
                PitData unique = pitDataQueryBuilder.unique();
                unique.setData(pitData.getData());
                pitDataDao.update(unique);
                return false;
            } else {
                pitDataDao.insert(pitData);
                return true;
            }
        }

        public QueryBuilder<PitData> query(Long robot_id, Long metric_id, Long event_id, Long user_id) {
            QueryBuilder<PitData> queryBuilder = getQueryBuilder();
            if (robot_id != null)
                queryBuilder.where(PitDataDao.Properties.Robot_id.eq(robot_id));
            if (metric_id != null)
                queryBuilder.where(PitDataDao.Properties.Metric_id.eq(event_id));
            if (user_id != null)
                queryBuilder.where(PitDataDao.Properties.User_id.eq(user_id));
            return queryBuilder;
        }

        public Metric getMetric(PitData pitData) {
            return getMetricsTable().load(pitData.getMetric_id());
        }

        public List<PitData> loadAll() {
            return pitDataDao.loadAll();
        }

        @Override
        public PitData load(long id) {
            return pitDataDao.load(id);
        }

        @Override
        public void delete(PitData model) {
            pitDataDao.delete(model);
        }


        @Override
        public void delete(List<PitData> models) {
            for (PitData data : models) {
                delete(data);
            }
        }

        @Override
        public QueryBuilder<PitData> getQueryBuilder() {
            return pitDataDao.queryBuilder();
        }


    }

    public class MatchDatas implements Table<MatchData> {
        public boolean insertMatchData(MatchData matchData) {
            matchData.setId(null);
            QueryBuilder<MatchData> matchDataQueryBuilder = matchDataDao.queryBuilder();
            matchDataQueryBuilder.where(MatchDataDao.Properties.Robot_id.eq(matchData.getRobot_id()));
            matchDataQueryBuilder.where(MatchDataDao.Properties.Metric_id.eq(matchData.getMetric_id()));
            matchDataQueryBuilder.where(MatchDataDao.Properties.Match_number.eq(matchData.getMatch_number()));
            matchDataQueryBuilder.where(MatchDataDao.Properties.Event_id.eq(matchData.getEvent_id()));
            matchDataQueryBuilder.where(MatchDataDao.Properties.Match_type.eq(matchData.getMatch_type()));

            long count = matchDataQueryBuilder.count();

            if (count > 0) {
                MatchData unique = matchDataQueryBuilder.unique();
                unique.setData(matchData.getData());
                matchDataDao.update(unique);
                return false;
            } else {
                matchDataDao.insert(matchData);
                return true;
            }
        }

        public QueryBuilder<MatchData> query(Long robotId, Long metricId, Integer match_number, Integer match_type, Long eventId, Long userId) {
            QueryBuilder<MatchData> matchDataQueryBuilder = getQueryBuilder();
            if (robotId != null)
                matchDataQueryBuilder.where(MatchDataDao.Properties.Robot_id.eq(robotId));
            if (metricId != null)
                matchDataQueryBuilder.where(MatchDataDao.Properties.Metric_id.eq(metricId));
            if (match_number != null)
                matchDataQueryBuilder.where(MatchDataDao.Properties.Match_number.eq(match_number));
            if (match_type != null)
                matchDataQueryBuilder.where(MatchDataDao.Properties.Match_type.eq(match_type));
            if (eventId != null)
                matchDataQueryBuilder.where(MatchDataDao.Properties.Event_id.eq(eventId));
            if (userId != null)
                matchDataQueryBuilder.where(MatchDataDao.Properties.User_id.eq(userId));
            return matchDataQueryBuilder;
        }

        public Metric getMetric(MatchData matchData) {
            return getMetricsTable().load(matchData.getMetric_id());
        }

        public List<MatchData> loadAll() {
            return matchDataDao.loadAll();
        }

        @Override
        public MatchData load(long id) {
            return matchDataDao.load(id);
        }

        @Override
        public void delete(MatchData model) {
            matchDataDao.delete(model);
        }


        @Override
        public void delete(List<MatchData> models) {
            for (MatchData model : models) {
                delete(model);
            }
        }

        @Override
        public QueryBuilder<MatchData> getQueryBuilder() {
            return matchDataDao.queryBuilder();
        }


    }

    public class Metrics implements Table<Metric> {
        public int getNumberOfMetrics(Game game, int metric_category) {
            QueryBuilder<Metric> metricQueryBuilder = metricDao.queryBuilder();
            metricQueryBuilder.where(MetricDao.Properties.Game_id.eq(game.getId()));
            metricQueryBuilder.where(MetricDao.Properties.Category.eq(metric_category));

            return (int) metricQueryBuilder.count();
        }

        public QueryBuilder<Metric> query(Integer category, Integer type, Long game_id) {
            QueryBuilder<Metric> queryBuilder = getQueryBuilder();
            if (category != null)
                queryBuilder.where(MetricDao.Properties.Category.eq(category));
            if (type != null)
                queryBuilder.where(MetricDao.Properties.Type.eq(type));
            if (game_id != null)
                queryBuilder.where(MetricDao.Properties.Game_id.eq(game_id));
            return queryBuilder;
        }

        public void insert(Metric metric) {
            metricDao.insertOrReplace(metric);
        }

        public List<MatchData> getMatchDataList(Metric metric) {
            metric.resetMatchDataList();
            return metric.getMatchDataList();
        }

        @Override
        public Metric load(long id) {
            return metricDao.load(id);
        }

        public List<PitData> getPitDataList(Metric metric) {
            metric.resetPitDataList();
            return metric.getPitDataList();
        }

        @Override
        public void delete(Metric metric) {
            metric.resetMatchDataList();
            metric.resetPitDataList();

            pitDataDao.deleteInTx(metric.getPitDataList());
            matchDataDao.deleteInTx(metric.getMatchDataList());
            metricDao.delete(metric);
        }

        @Override
        public void delete(List<Metric> models) {
            for (Metric model : models) {
                delete(model);
            }
        }

        @Override
        public QueryBuilder<Metric> getQueryBuilder() {
            return metricDao.queryBuilder();
        }
    }

    public class Robots implements Table<Robot> {
        public Game getGame(Robot mRobot) {
            return getGamesTable().load(mRobot.getGame_id());
        }

        public Team getTeam(Robot robot) {
            return teamDao.load(robot.getTeam_id());
        }

        public List<RobotComment> getRobotComments() {
            List<Robot> robots = robotDao.loadAll();
            List<RobotComment> robotComments = new ArrayList<>();

            for (Robot robot : robots) {
                robotComments.add(getRobotComment(robot));
            }

            return robotComments;
        }

        public RobotComment getRobotComment(Robot robot) {
            return new RobotComment(robot.getId(), robot.getComments());
        }

        public QueryBuilder<Robot> query(@Nullable Long team_number, @Nullable Long game_id) {
            QueryBuilder<Robot> robotQueryBuilder = getRobotsTable().getQueryBuilder();
            if (team_number != null)
                robotQueryBuilder.where(RobotDao.Properties.Team_id.eq(team_number));
            if (game_id != null)
                robotQueryBuilder.where(RobotDao.Properties.Game_id.eq(game_id));
            return robotQueryBuilder;
        }

        @Override
        public void delete(List<Robot> robots) {
            for (Robot robot : robots) {
                delete(robot);
            }
        }

        public void insert(Robot robot) {
            robotDao.insertOrReplace(robot);
        }

        public void update(Robot robot) {
            robotDao.update(robot);
        }

        public List<RobotEvent> getRobotEvents(Robot robot) {
            robot.resetRobotEventList();
            return robot.getRobotEventList();
        }

        @Override
        public QueryBuilder<Robot> getQueryBuilder() {
            return robotDao.queryBuilder();
        }

        @Override
        public Robot load(long id) {
            return robotDao.load(id);
        }


        @Override
        public void delete(Robot robot) {
            robotDao.delete(robot);
        }


    }

    public class RobotEvents implements Table<RobotEvent> {
        public Team getTeam(RobotEvent robotEvent) {
            return teamDao.load(getRobot(robotEvent).getTeam_id());
        }

        public Robot getRobot(RobotEvent robotEvent) {
            return getRobotsTable().load(robotEvent.getRobot_id());
        }

        public void insert(RobotEvent robotEvent) {
            robotEventDao.insert(robotEvent);
        }

        @Override
        public RobotEvent load(long id) {
            return robotEventDao.load(id);
        }


        @Override
        public void delete(RobotEvent model) {
            robotEventDao.delete(model);
        }

        @Override
        public void delete(List<RobotEvent> models) {
            for (RobotEvent model : models) {
                delete(model);
            }
        }


        @Override
        public QueryBuilder<RobotEvent> getQueryBuilder() {
            return robotEventDao.queryBuilder();
        }


    }

    public class Matches implements Table<Match> {
        public List<Team> getTeams(Match match) {
            JsonObject alliances = JSON.getAsJsonObject(match.getData()).get("alliances").getAsJsonObject();
            List<Team> teams = new ArrayList<>();
            JsonArray red = alliances.get("red").getAsJsonObject().get("teams").getAsJsonArray();
            JsonArray blue = alliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
            teams.add(getTeamsTable().load(Long.parseLong(red.get(0).getAsString().replace("frc", ""))));
            teams.add(getTeamsTable().load(Long.parseLong(red.get(1).getAsString().replace("frc", ""))));
            teams.add(getTeamsTable().load(Long.parseLong(red.get(2).getAsString().replace("frc", ""))));
            teams.add(getTeamsTable().load(Long.parseLong(blue.get(0).getAsString().replace("frc", ""))));
            teams.add(getTeamsTable().load(Long.parseLong(blue.get(1).getAsString().replace("frc", ""))));
            teams.add(getTeamsTable().load(Long.parseLong(blue.get(2).getAsString().replace("frc", ""))));
            return teams;
        }

        public void insert(Match match) {
            matchDao.insertOrReplace(match);
        }

        public QueryBuilder<Match> query(Integer match_number, String key, Long event_id, String type) {
            QueryBuilder<Match> queryBuilder = getQueryBuilder();
            if (match_number != null)
                queryBuilder.where(MatchDao.Properties.Match_number.eq(match_number));
            if (key != null)
                queryBuilder.where(MatchDao.Properties.Match_key.eq(key));
            if (event_id != null)
                queryBuilder.where(MatchDao.Properties.Event_id.eq(event_id));
            if (type != null)
                queryBuilder.where(MatchDao.Properties.Event_id.eq(type));
            return queryBuilder;
        }

        @Override
        public Match load(long id) {
            return matchDao.load(id);
        }


        @Override
        public void delete(Match match) {
            matchDao.delete(match);
        }

        @Override
        public void delete(List<Match> models) {
            for (Match model : models) {
                delete(model);
            }
        }

        @Override
        public QueryBuilder<Match> getQueryBuilder() {
            return matchDao.queryBuilder();
        }


    }

    public class Teams implements Table<Team> {
        /**
         * Inserts team and Robot, and Robot Event
         *
         * @param team
         */
        public void insertNew(Team team, Event event) {
            boolean team_added = teamDao.load(team.getNumber()) != null;
            if (team_added) {
                team = teamDao.load(team.getNumber());
            } else {
                daoSession.insert(team);
            }

            //Check robot
            QueryBuilder<Robot> robotQueryBuilder = getRobotsTable().getQueryBuilder();
            robotQueryBuilder.where(RobotDao.Properties.Game_id.eq(event.getGame_id()));
            robotQueryBuilder.where(RobotDao.Properties.Team_id.eq(team.getNumber()));
            Robot robot = robotQueryBuilder.unique();
            boolean robot_added = robot != null;

            if (!robot_added) {
                robot = new Robot(null, team.getNumber(), event.getGame_id(), null, null, new Date());
                daoSession.insert(robot);
            }

            QueryBuilder<RobotEvent> robotEventQueryBuilder = getRobotEvents().getQueryBuilder();
            robotEventQueryBuilder.where(RobotEventDao.Properties.Event_id.eq(event.getGame_id()));
            robotEventQueryBuilder.where(RobotEventDao.Properties.Robot_id.eq(robot.getId()));
            RobotEvent robotEvent = robotEventQueryBuilder.unique();
            boolean robot_event_exists = robotEvent != null;

            if (!robot_event_exists) {
                robotEvent = new RobotEvent(null, robot.getId(), event.getId(), null);
                daoSession.insert(robotEvent);
            }
        }

        public void insert(Team team) {
            teamDao.insertOrReplace(team);
        }

        @Override
        public Team load(long id) {
            return teamDao.load(id);
        }

        @Override
        public void delete(Team model) {
            teamDao.delete(model);
        }

        @Override
        public void delete(List<Team> models) {
            for (Team model : models) {
                delete(model);
            }
        }

        @Override
        public QueryBuilder<Team> getQueryBuilder() {
            return teamDao.queryBuilder();
        }
    }

    public class Users implements Table<User> {

        public List<User> loadAll() {
            return userDao.loadAll();
        }

        public void insert(User user) {
            userDao.insertOrReplace(user);
        }

        @Override
        public User load(long id) {
            return userDao.load(id);
        }


        @Override
        public void delete(User model) {
            userDao.delete(model);
        }

        @Override
        public void delete(List<User> models) {
            for (User model : models) {
                delete(model);
            }
        }

        @Override
        public QueryBuilder<User> getQueryBuilder() {
            return userDao.queryBuilder();
        }
    }
}
