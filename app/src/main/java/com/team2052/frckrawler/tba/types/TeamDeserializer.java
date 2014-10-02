package com.team2052.frckrawler.tba.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.database.models.Team;

import java.lang.reflect.Type;

/**
 * @author Adam
 */
public class TeamDeserializer implements JsonDeserializer<Team>
{
    @Override
    public Team deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        final Team team = new Team();
        final JsonObject object = json.getAsJsonObject();
        if (object.has("key") && !object.get("key").isJsonNull()) {
            team.teamKey = object.get("key").getAsString();
        }

        if (object.has("team_number") && !object.get("team_number").isJsonNull()) {
            team.number = object.get("team_number").getAsInt();
        }

        if (object.has("nickname") && !object.get("nickname").isJsonNull()) {
            team.name = object.get("nickname").getAsString();
        }

        if (object.has("location") && !object.get("location").isJsonNull()) {
            team.location = object.get("location").getAsString();
        }

        if (object.has("website") && !object.get("website").isJsonNull()) {
            team.website = object.get("website").getAsString();
        }

        if (object.has("rookie_year") && !object.get("rookie_year").isJsonNull()) {
            team.rookieYear = object.get("rookie_year").getAsInt();
        }

        return team;
    }
}
