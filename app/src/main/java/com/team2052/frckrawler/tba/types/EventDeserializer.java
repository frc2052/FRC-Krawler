package com.team2052.frckrawler.tba.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.database.models.Event;

import java.lang.reflect.Type;

/**
 * @author Adam
 */
public class EventDeserializer implements JsonDeserializer<Event> {
    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Event event = new Event();
        if (object.has("key")) {
            event.fmsId = object.get("key").getAsString();
        }

        if (object.has("name")) {
            event.name = object.get("name").getAsString();
        }

        if (object.get("location").isJsonNull()) {
            event.location = "";
        } else {
            event.location = object.get("location").getAsString();
        }

        return event;
    }
}
