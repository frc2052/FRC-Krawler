/*
package com.team2052.frckrawler.data.tba.v3.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.data.tba.v3.JSON;
import com.team2052.frckrawler.models.Event;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EventDeserializer implements JsonDeserializer<Event> {
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Event event = new Event();
        if (object.has("key")) {
            event.setFmsid(object.get("key").getAsString());
        }

        if (object.has("name")) {
            event.setName(object.get("name").getAsString());
        }


        JsonObject data = new JsonObject();
        if (object.get("address").isJsonNull()) {
            data.addProperty("address", "Location Unknown");
        } else {
            data.addProperty("address", object.get("address").getAsString());
        }

        event.setData(JSON.getGson().toJson(data));

        //Parse the date
        if (!object.get("start_date").isJsonNull()) {
            try {
                event.setDate(format.parse(object.get("start_date").toString().replace("\"", "")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            event.setDate(new Date(0, 0, 0));
        }

        return event;
    }
}
*/
