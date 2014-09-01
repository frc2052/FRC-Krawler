package com.team2052.frckrawler.tba.types;

import com.activeandroid.query.Select;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.team2052.frckrawler.database.models.Alliance;
import com.team2052.frckrawler.database.models.Team;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Adam
 */
public class AllianceDeserializer implements JsonDeserializer<Alliance>{

    @Override
    public Alliance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Alliance alliance = new Alliance();
        final JsonObject object = json.getAsJsonObject();
        if(object.has("blue") &&!object.get("blue").isJsonNull()){
            JsonObject blue = object.get("blue").getAsJsonObject();
            if(blue.has("score") && !blue.get("score").isJsonNull()){
                alliance.blueScore = blue.get("score").getAsInt();
            }
            if(blue.has("teams") && !blue.get("teams").isJsonNull()){
                JsonArray jBlueTeams = blue.get("teams").getAsJsonArray();
                List<Team> blue1 = new Select().from(Team.class).where("TeamKey = ?", jBlueTeams.get(0).getAsString()).execute();
                List<Team> blue2 = new Select().from(Team.class).where("TeamKey = ?", jBlueTeams.get(1).getAsString()).execute();
                List<Team> blue3 = new Select().from(Team.class).where("TeamKey = ?", jBlueTeams.get(2).getAsString()).execute();
                alliance.blue1 = blue1.get(0);
                alliance.blue2 = blue2.get(0);
                alliance.blue3 = blue3.get(0);
            }
        }

        if(object.has("red") &&!object.get("red").isJsonNull()){
            JsonObject red = object.get("red").getAsJsonObject();
            if(red.has("score") && !red.get("score").isJsonNull()){
                alliance.redScore = red.get("score").getAsInt();
            }
            if(red.has("teams") && !red.get("teams").isJsonNull()){
                JsonArray jRedTeams = red.get("teams").getAsJsonArray();
                List<Team> red1 = new Select().from(Team.class).where("TeamKey = ?", jRedTeams.get(0).getAsString()).execute();
                List<Team> red2 = new Select().from(Team.class).where("TeamKey = ?", jRedTeams.get(1).getAsString()).execute();
                List<Team> red3 = new Select().from(Team.class).where("TeamKey = ?", jRedTeams.get(2).getAsString()).execute();
                alliance.red1 = red1.get(0);
                alliance.red2 = red2.get(0);
                alliance.red3 = red3.get(0);
            }
        }
        //Generate a remote id so we don't unique conflicts
        alliance.setRemoteId();
        return alliance;
    }
}
