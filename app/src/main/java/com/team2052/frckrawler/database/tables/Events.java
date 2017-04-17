package com.team2052.frckrawler.database.tables;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchDatum;
import com.team2052.frckrawler.db.PitDatum;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.tba.JSON;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Events extends AbstractTable<Event, EventDao> {
    public Events(EventDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public List<Robot> getRobots(Event event) {
        List<Robot> robots = new ArrayList<>();
        for (RobotEvent robotEvent : getRobotEvents(event)) {
            robots.add(dbManager.getRobotEventsTable().getRobot(robotEvent));
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
        dbManager.getMatchDataTable().delete(getMatchData(model));
        dbManager.getPitDataTable().delete(getPitData(model));
        dbManager.getMatchCommentsTable().delete(getMatchComments(model));
        dbManager.getMatchesTable().delete(getMatches(model));
        dbManager.getRobotEventsTable().delete(getRobotEvents(model));
        dao.delete(model);
    }

    public List<Event> getAllEvents() {
        return dao.loadAll();
    }


    public List<Match> getMatches(Event event) {
        event.resetMatchList();
        return event.getMatchList();
    }

    public List<MatchDatum> getMatchData(Event event) {
        event.resetMatchDatumList();
        return event.getMatchDatumList();
    }

    public List<PitDatum> getPitData(Event event) {
        event.resetPitDatumList();
        return event.getPitDatumList();
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
            teams.add(dbManager.getRobotEventsTable().getTeam(robotEvent));
        }
        Collections.sort(teams, (lhs, rhs) -> Double.compare(lhs.getNumber(), rhs.getNumber()));
        return teams;
    }

    public static Optional<String> getEventLocation(Event event) {
        if (event == null || event.getData() == null)
            return Optional.absent();

        JsonObject data = JSON.getAsJsonObject(event.getData());

        if (data.has("location")) {
            return Optional.of(data.get("location").getAsString());
        }
        return Optional.absent();
    }
}
