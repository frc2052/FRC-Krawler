package com.team2052.frckrawler.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.team2052.frckrawler.database.deserializers.TeamDeserializer;
import com.team2052.frckrawler.database.models.Team;

/**
 * @author Adam
 */
public class JSONManager {

    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Team.class, new TeamDeserializer());
            gson = builder.create();
        }
        return gson;
    }
}
