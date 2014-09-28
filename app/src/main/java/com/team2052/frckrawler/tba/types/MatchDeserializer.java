package com.team2052.frckrawler.tba.types;

import com.activeandroid.query.Select;
import com.google.gson.*;
import com.team2052.frckrawler.database.models.*;
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
            match.key = object.get("key").getAsString();
        }

        if (object.has("comp_level") && !object.get("comp_level").isJsonNull()) {
            match.matchType = object.get("comp_level").getAsString();
        }

        if (object.has("match_number") && !object.get("match_number").isJsonNull()) {
            match.matchNumber = object.get("match_number").getAsInt();
        }

        if (object.has("event_key") && !object.get("event_key").isJsonNull()) {
            match.event = (Event) new Select().from(Event.class).where("FMSId = ?", object.get("event_key").getAsString()).execute().get(0);
        }

        if (object.has("alliances") && !object.get("alliances").isJsonNull()) {
            match.alliance = JSON.getGson().fromJson(object.get("alliances").getAsJsonObject(), Alliance.class);
        }
        match.setRemoteId();
        return match;
    }
}
