package com.team2052.frckrawler.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.team2052.frckrawler.database.tables.Events;
import com.team2052.frckrawler.database.tables.Games;
import com.team2052.frckrawler.database.tables.MatchComments;
import com.team2052.frckrawler.database.tables.MatchData;
import com.team2052.frckrawler.database.tables.Matches;
import com.team2052.frckrawler.database.tables.Metrics;
import com.team2052.frckrawler.database.tables.PitData;
import com.team2052.frckrawler.database.tables.RobotEvents;
import com.team2052.frckrawler.database.tables.Robots;
import com.team2052.frckrawler.database.tables.Teams;
import com.team2052.frckrawler.db.DaoMaster;
import com.team2052.frckrawler.db.DaoSession;


/**
 * Used to keep a clean database
 * There is no CASCADE ON DELETE function with greenDAO, so we have to configure it manually.
 *
 * @author Adam
 * @since 10/7/2014
 */
public class DBManager {
    protected final DaoSession daoSession;
    private final DaoMaster daoMaster;
    private final Games mGames;
    private final Events mEvents;
    private final Robots mRobots;
    private final Metrics mMetrics;
    private final RobotEvents mRobotEvents;
    private final MatchComments mMatchComments;
    private final MatchData mMatchData;
    private final PitData mPitDatas;
    private final Matches mMatches;
    private final Teams mTeams;
    protected Context context;

    DBManager(Context context) {
        this.context = context;

        DaoMaster.OpenHelper helper = new DatabaseHelper(context, "frc-krawler-database-v3", null);

        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        mGames = new Games(daoSession.getGameDao(), this);
        mEvents = new Events(daoSession.getEventDao(), this);
        mRobots = new Robots(daoSession.getRobotDao(), this);
        mMetrics = new Metrics(daoSession.getMetricDao(), this);
        mRobotEvents = new RobotEvents(daoSession.getRobotEventDao(), this);
        mMatchComments = new MatchComments(daoSession.getMatchCommentDao(), this);
        mMatchData = new MatchData(daoSession.getMatchDatumDao(), this);
        mPitDatas = new PitData(daoSession.getPitDatumDao(), this);
        mMatches = new Matches(daoSession.getMatchDao(), this);
        mTeams = new Teams(daoSession.getTeamDao(), this);
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

    public RobotEvents getRobotEventsTable() {
        return mRobotEvents;
    }

    public MatchComments getMatchCommentsTable() {
        return mMatchComments;
    }

    public MatchData getMatchDataTable() {
        return mMatchData;
    }

    public PitData getPitDataTable() {
        return mPitDatas;
    }

    public Matches getMatchesTable() {
        return mMatches;
    }

    public Teams getTeamsTable() {
        return mTeams;
    }
}
