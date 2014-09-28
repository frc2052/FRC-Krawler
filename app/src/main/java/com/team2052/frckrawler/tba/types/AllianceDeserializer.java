package com.team2052.frckrawler.tba.types;

import com.activeandroid.query.Select;
import com.google.gson.*;
import com.team2052.frckrawler.database.models.*;

import java.lang.reflect.Type;

/**
 * @author Adam
 */
public class AllianceDeserializer implements JsonDeserializer<Alliance>
{

    @Override
    public Alliance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        final Alliance alliance = new Alliance();
        final JsonObject object = json.getAsJsonObject();
        if (object.has("blue") && !object.get("blue").isJsonNull()) {
            JsonObject blue = object.get("blue").getAsJsonObject();
            if (blue.has("score") && !blue.get("score").isJsonNull()) {
                alliance.blueScore = blue.get("score").getAsInt();
            }
            if (blue.has("teams") && !blue.get("teams").isJsonNull()) {
                JsonArray jBlueTeams = blue.get("teams").getAsJsonArray();
                Team blue1 = new Select().from(Team.class).where("TeamKey = ?", jBlueTeams.get(0).getAsString()).executeSingle();
                Team blue2 = new Select().from(Team.class).where("TeamKey = ?", jBlueTeams.get(1).getAsString()).executeSingle();
                Team blue3 = new Select().from(Team.class).where("TeamKey = ?", jBlueTeams.get(2).getAsString()).executeSingle();
                alliance.blue1 = blue1;
                alliance.blue2 = blue2;
                alliance.blue3 = blue3;
            }
        }

        if (object.has("red") && !object.get("red").isJsonNull()) {
            JsonObject red = object.get("red").getAsJsonObject();
            if (red.has("score") && !red.get("score").isJsonNull()) {
                alliance.redScore = red.get("score").getAsInt();
            }
            if (red.has("teams") && !red.get("teams").isJsonNull()) {
                JsonArray jRedTeams = red.get("teams").getAsJsonArray();
                Team red1 = new Select().from(Team.class).where("TeamKey = ?", jRedTeams.get(0).getAsString()).executeSingle();
                Team red2 = new Select().from(Team.class).where("TeamKey = ?", jRedTeams.get(1).getAsString()).executeSingle();
                Team red3 = new Select().from(Team.class).where("TeamKey = ?", jRedTeams.get(2).getAsString()).executeSingle();
                alliance.red1 = red1;
                alliance.red2 = red2;
                alliance.red3 = red3;
            }
        }
        //Generate a remote id so we don't unique conflicts
        alliance.setRemoteId();
        return alliance;
    }
}
