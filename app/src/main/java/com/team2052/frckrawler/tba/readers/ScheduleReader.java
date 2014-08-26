package com.team2052.frckrawler.tba.readers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.team2052.frckrawler.tba.types.TBAMatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ScheduleReader {
    private static String SCHEDULE_URL_BOTTOM = "http://www.thebluealliance.com/api/v2" +
            "/event/";
    private static String SCHEDULE_URL_TOP = "/matches?X-TBA-App-Id=" +
            "frckrawler:FRCKrawler-system:v02";
    private String eventID;

    public ScheduleReader(String tbaEventID) {
        eventID = tbaEventID;
    }

    public TBAMatch[] getMatches() throws IOException {
        String scheduleURL = SCHEDULE_URL_BOTTOM + eventID + SCHEDULE_URL_TOP;
        URL url = new URL(scheduleURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the connection
        conn.connect();
        InputStream is = conn.getInputStream();
        String jsonContent = readJSON(is);
        //Parse the content
        JsonParser parser = new JsonParser();
        JsonElement arrElem = parser.parse(jsonContent);
        JsonArray arr = arrElem.getAsJsonArray();
        Gson gson = new Gson();
        List<TBAMatch> matchArr = new ArrayList<TBAMatch>();
        JsonElement elem;
        TBAMatch match;
        for (int i = 0; i < arr.size(); i++) {
            elem = arr.get(i);
            match = gson.fromJson(elem, TBAMatch.class);
            if (match.getCompLevel().equals("qm")) {
                matchArr.add(match);
            }
        }
        return matchArr.toArray(new TBAMatch[0]);
    }

    public String readJSON(InputStream stream) throws IOException,
            UnsupportedEncodingException {
        InputStream in = stream;
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();
        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }

        return sb.toString();
    }
}
