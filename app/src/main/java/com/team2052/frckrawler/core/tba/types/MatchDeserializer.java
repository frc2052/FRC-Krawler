package com.team2052.frckrawler.core.tba.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.Match;

import java.lang.reflect.Type;

/**
 * @author Adam
 */
public class MatchDeserializer implements JsonDeserializer<Match> {

    @Override
    public Match deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
            match.setEventId(JSON.get_daoSession().getDaoSession().getEventDao().queryBuilder().where(EventDao.Properties.Fmsid.eq(object.get("event_key").getAsString())).unique().getId());
        }

        JsonObject alliances = object.get("alliances").getAsJsonObject();
        JsonObject data = new JsonObject();
        data.add("alliances", alliances);
        match.setData(JSON.getGson().toJson(data));
        return match;
    }
}
