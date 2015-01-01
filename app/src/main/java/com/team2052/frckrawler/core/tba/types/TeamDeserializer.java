package com.team2052.frckrawler.core.tba.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.core.tba.JSON;

import java.lang.reflect.Type;

/**
 * @author Adam
 */
public class TeamDeserializer implements JsonDeserializer<Team> {
    @Override
    public Team deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Team team = new Team();
        final JsonObject object = json.getAsJsonObject();
        if (object.has("key") && !object.get("key").isJsonNull()) {
            team.setTeamkey(object.get("key").getAsString());
        }

        if (object.has("team_number") && !object.get("team_number").isJsonNull()) {
            team.setNumber(object.get("team_number").getAsLong());
        }

        if (object.has("nickname") && !object.get("nickname").isJsonNull()) {
            team.setName(object.get("nickname").getAsString());
        }

        if (object.has("location") && !object.get("location").isJsonNull()) {
            team.setLocation(object.get("location").getAsString());
        }

        JsonObject data = new JsonObject();

        if (object.has("website") && !object.get("website").isJsonNull()) {
            data.addProperty("website", object.get("website").getAsString());
        }

        if (object.has("rookie_year") && !object.get("rookie_year").isJsonNull()) {
            data.addProperty("rookie_year", object.get("rookie_year").getAsInt());
        }

        if (object.has("name") && !object.get("name").isJsonNull()) {
            data.addProperty("long_name", object.get("name").getAsString());
        }

        team.setData(JSON.getGson().toJson(data));

        return team;
    }
}
