package com.team2052.frckrawler.data.tba.v3.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.data.tba.v3.JSON;
import com.team2052.frckrawler.models.Match;

import java.lang.reflect.Type;

public class MatchDeserializer implements JsonDeserializer<Match> {

    @Override
    public Match deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Match match = new Match();
        final JsonObject object = json.getAsJsonObject();
        if (object.has("key") && !object.get("key").isJsonNull()) {
            match.setMatch_key(object.get("key").getAsString());
        }

        if (object.has("comp_level") && !object.get("comp_level").isJsonNull()) {
            match.setMatch_type(object.get("comp_level").getAsString());
        }

        if (object.has("match_number") && !object.get("match_number").isJsonNull()) {
            match.setMatch_number(object.get("match_number").getAsInt());
        }

        if (object.has("event_key") && !object.get("event_key").isJsonNull()) {
            match.setEvent_id(JSON.get_daoSession().getEventsTable().query(object.get("event_key").getAsString(), null).unique().getId());
        }

        JsonObject alliances = object.get("alliances").getAsJsonObject();
        JsonObject data = new JsonObject();
        data.add("alliances", alliances);
        match.setData(JSON.getGson().toJson(data));
        return match;
    }
}
