package com.team2052.frckrawler.database.tables;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.tba.JSON;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class Matches extends Table<Match, MatchDao> {
    public Matches(MatchDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public List<Team> getTeams(Match match) {
        JsonObject alliances = JSON.getAsJsonObject(match.getData()).get("alliances").getAsJsonObject();
        List<Team> teams = new ArrayList<>();
        JsonArray red = alliances.get("red").getAsJsonObject().get("teams").getAsJsonArray();
        JsonArray blue = alliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();

        teams.add(dbManager.getTeamsTable().load(Long.parseLong(red.get(0).getAsString().replace("frc", ""))));
        teams.add(dbManager.getTeamsTable().load(Long.parseLong(red.get(1).getAsString().replace("frc", ""))));
        teams.add(dbManager.getTeamsTable().load(Long.parseLong(red.get(2).getAsString().replace("frc", ""))));
        teams.add(dbManager.getTeamsTable().load(Long.parseLong(blue.get(0).getAsString().replace("frc", ""))));
        teams.add(dbManager.getTeamsTable().load(Long.parseLong(blue.get(1).getAsString().replace("frc", ""))));
        teams.add(dbManager.getTeamsTable().load(Long.parseLong(blue.get(2).getAsString().replace("frc", ""))));
        return teams;
    }

    public void insert(Match match) {
        dao.insertOrReplace(match);
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
        return dao.load(id);
    }


    @Override
    public void delete(Match match) {
        dao.delete(match);
    }
}
