package com.team2052.frckrawler.database.deserializers;

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
public class TeamDeserializer implements JsonDeserializer<Team>{
    @Override
    public Team deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Team team = new Team();
        if(object.has("key") && !object.get("key").isJsonNull()){
            team.teamKey = object.get("key").getAsString();
        }
        return team;
    }
}
