package com.team2052.frckrawler.tba;

import com.google.gson.*;
import com.team2052.frckrawler.database.models.*;
import com.team2052.frckrawler.tba.types.*;

/**
 * @author Adam
 */
public class JSON
{
    private static Gson gson;
    private static JsonParser parser;

    public static Gson getGson()
    {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());
            gsonBuilder.registerTypeAdapter(Team.class, new TeamDeserializer());
            gsonBuilder.registerTypeAdapter(Alliance.class, new AllianceDeserializer());
            gsonBuilder.registerTypeAdapter(Match.class, new MatchDeserializer());
            gson = gsonBuilder.create();
        }
        return gson;
    }

    public static JsonObject getAsJsonObject(String in)
    {
        if (in == null || in.equals("")) {
            return new JsonObject();
        }
        JsonElement e = getParser().parse(in);
        if (e == null || e.isJsonNull()) {
            return new JsonObject();
        }
        return e.getAsJsonObject();
    }

    public static JsonArray getAsJsonArray(String in)
    {
        if (in == null || in.equals("")) {
            return new JsonArray();
        }
        return getParser().parse(in).getAsJsonArray();
    }

    public static JsonParser getParser()
    {
        if (parser == null) {
            parser = new JsonParser();
        }
        return parser;
    }
}
