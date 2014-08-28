package com.team2052.frckrawler.tba.readers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.team2052.frckrawler.tba.types.TBATeam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TeamReader {

    private String teamsURL;

    public TeamReader(String faEventID) {
        teamsURL = "http://www.thebluealliance.com/api/v2/event/" + faEventID + "/teams?X-TBA-App-Id=frckrawler:frckrawler-scouting-system:v18";
    }

    public TBATeam[] readTeams() throws IOException {
        URL url = new URL(teamsURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Start the connection
        conn.connect();
        InputStream is = conn.getInputStream();
        String jsonContent = readJSON(is);

        //Parse the Json array
        JsonParser jParser = new JsonParser();
        JsonArray array = jParser.parse(jsonContent).getAsJsonArray();
        TBATeam[] faTeams = new TBATeam[array.size()];

        //Get each FATeam out of the array
        Gson parser = new Gson();
        for (int i = 0; i < array.size(); i++) {
            faTeams[i] = parser.fromJson(array.get(i), TBATeam.class);
        }

        if (is != null)
            try {
                is.close();
            } catch (IOException e) {
            }

        return faTeams;
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
