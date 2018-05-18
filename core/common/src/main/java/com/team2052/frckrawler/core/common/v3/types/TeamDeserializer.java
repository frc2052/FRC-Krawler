/*
package com.team2052.frckrawler.core.common.v3.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.core.common.v3.JSON;
import com.team2052.frckrawler.core.data.models.Team;

import java.lang.reflect.Type;

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

        if (object.has("location") && !object.get("location").isJsonNull()) {
            data.addProperty("location", object.get("location").getAsString());
        }

        team.setData(JSON.getGson().toJson(data));

        return team;
    }
}
*/
