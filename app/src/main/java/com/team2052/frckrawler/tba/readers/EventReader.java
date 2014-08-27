package com.team2052.frckrawler.tba.readers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.team2052.frckrawler.tba.types.TBAEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class EventReader {

    private static final String EVENT_URL = "http://www.thebluealliance.com" + "/api/v2/events/?X-TBA-App-Id=frckrawler:frckrawler-scouting-system:v2";

    public EventReader() {
    }

    public TBAEvent[] readEvents() throws IOException {
        // Set up the connection
        URL url = new URL(EVENT_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the connection
        conn.connect();
        // Get results
        InputStream is = conn.getInputStream();

        // Parse results
        String jsonContent = readTBAData(is);

        // Parse the Json array
        JsonParser jParser = new JsonParser();
        JsonArray array = jParser.parse(jsonContent).getAsJsonArray();
        TBAEvent[] tbaEvents = new TBAEvent[array.size()];

        // Get each FATeam out of the array
        Gson parser = new Gson();
        for (int i = 0; i < array.size(); i++) {
            tbaEvents[i] = parser.fromJson(array.get(i), TBAEvent.class);
        }
        return tbaEvents;
    }

    private String readTBAData(InputStream stream) throws IOException {
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
