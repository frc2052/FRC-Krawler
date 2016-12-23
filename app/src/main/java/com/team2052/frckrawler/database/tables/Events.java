package com.team2052.frckrawler.database.tables;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.Team;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Events extends Table<Event, EventDao> {
    private RxDBManager rxDbManager;

    public Events(EventDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public List<Robot> getRobots(Event event) {
        List<Robot> robots = new ArrayList<>();
        for (RobotEvent robotEvent : getRobotEvents(event)) {
            robots.add(rxDbManager.getRobotEvents().getRobot(robotEvent));
        }
        return robots;
    }

    public List<RobotEvent> getRobotEvents(Event event) {
        event.resetRobotEventList();
        return event.getRobotEventList();
    }

    @Override
    public Event load(long id) {
        return dao.load(id);
    }

    public void insert(Event event) {
        dao.insertOrReplace(event);
    }

    @Override
    public void delete(Event model) {
        rxDbManager.getMatchDataTable().delete(getMatchData(model));
        rxDbManager.getPitDataTable().delete(getPitData(model));
        rxDbManager.getMatchComments().delete(getMatchComments(model));
        rxDbManager.getMatchesTable().delete(getMatches(model));
        rxDbManager.getRobotEvents().delete(getRobotEvents(model));
        dao.delete(model);
    }

    public List<Event> getAllEvents() {
        return dao.loadAll();
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

    public QueryBuilder<Event> query(String fms_id, Long game_id) {
        QueryBuilder<Event> queryBuilder = getQueryBuilder();
        if (fms_id != null)
            queryBuilder.where(EventDao.Properties.Fmsid.eq(fms_id));
        if (game_id != null)
            queryBuilder.where(EventDao.Properties.Game_id.eq(game_id));
        return queryBuilder;
    }

    public List<Team> getTeamsAtEvent(Event event) {
        List<RobotEvent> robotEventList = event.getRobotEventList();
        List<Team> teams = Lists.newArrayList();
        for (RobotEvent robotEvent : robotEventList) {
            teams.add(rxDbManager.getRobotEvents().getTeam(robotEvent));
        }
        Collections.sort(teams, (lhs, rhs) -> Double.compare(lhs.getNumber(), rhs.getNumber()));
        return teams;
    }
}
