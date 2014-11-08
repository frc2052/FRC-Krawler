package com.team2052.frckrawler.tba.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.TeamDao;
import com.team2052.frckrawler.tba.JSON;

import java.lang.reflect.Type;

/**
 * @author Adam
 */
public class MatchDeserializer implements JsonDeserializer<Match>
{

    @Override
    public Match deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        final Match match = new Match();
        final JsonObject object = json.getAsJsonObject();
        if (object.has("key") && !object.get("key").isJsonNull()) {
            match.setKey(object.get("key").getAsString());
        }

        if (object.has("comp_level") && !object.get("comp_level").isJsonNull()) {
            match.setType(object.get("comp_level").getAsString());
        }

        if (object.has("match_number") && !object.get("match_number").isJsonNull()) {
            match.setNumber(object.get("match_number").getAsInt());
        }

        if (object.has("event_key") && !object.get("event_key").isJsonNull()) {
            match.setEvent(JSON.get_daoSession().getEventDao().queryBuilder().where(EventDao.Properties.Fmsid.eq(object.get("event_key").getAsString())).unique());
        }

        JsonObject alliances = object.get("alliances").getAsJsonObject();
        if (alliances.has("blue") && !alliances.get("blue").isJsonNull()) {
            JsonObject blue = alliances.get("blue").getAsJsonObject();
            if (blue.has("score") && !blue.get("score").isJsonNull()) {
                match.setBluescore(blue.get("score").getAsInt());
            }
            if (blue.has("teams") && !blue.get("teams").isJsonNull()) {
                JsonArray jBlueTeams = blue.get("teams").getAsJsonArray();
                match.setBlue1(JSON.get_daoSession().getTeamDao().queryBuilder().where(TeamDao.Properties.Teamkey.eq(jBlueTeams.get(0).getAsString())).unique());
                match.setBlue2(JSON.get_daoSession().getTeamDao().queryBuilder().where(TeamDao.Properties.Teamkey.eq(jBlueTeams.get(1).getAsString())).unique());
                match.setBlue3(JSON.get_daoSession().getTeamDao().queryBuilder().where(TeamDao.Properties.Teamkey.eq(jBlueTeams.get(2).getAsString())).unique());
            }
        }

        if (alliances.has("red") && !alliances.get("red").isJsonNull()) {
            JsonObject blue = alliances.get("red").getAsJsonObject();
            if (blue.has("score") && !blue.get("score").isJsonNull()) {
                match.setRedscore(blue.get("score").getAsInt());
            }
            if (blue.has("teams") && !blue.get("teams").isJsonNull()) {
                JsonArray jBlueTeams = blue.get("teams").getAsJsonArray();
                match.setRed1(JSON.get_daoSession().getTeamDao().queryBuilder().where(TeamDao.Properties.Teamkey.eq(jBlueTeams.get(0).getAsString())).unique());
                match.setRed2(JSON.get_daoSession().getTeamDao().queryBuilder().where(TeamDao.Properties.Teamkey.eq(jBlueTeams.get(1).getAsString())).unique());
                match.setRed3(JSON.get_daoSession().getTeamDao().queryBuilder().where(TeamDao.Properties.Teamkey.eq(jBlueTeams.get(2).getAsString())).unique());
            }
        }
        return match;
    }
}
