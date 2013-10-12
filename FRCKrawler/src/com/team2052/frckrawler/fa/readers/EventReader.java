package com.team2052.frckrawler.fa.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import com.team2052.frckrawler.fa.types.FAEvent;

public class EventReader {
	
	private static final String EVENT_URL = "http://www.thefirstalliance.org" +
			"/api/api.json.php?action=list-events";
	
	public EventReader() {
		
	}
	
	public FAEvent[] readEvents() throws IOException {
		URL url = new URL(EVENT_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);

		// Start the connection
		conn.connect();
		InputStream is = conn.getInputStream();
		String jsonContent = readJSON(is);
		FAEvent[] faEvents;
		
		try {
			//Parse the Json array
			JsonParser jParser = new JsonParser();
			JsonObject dataObject = jParser.parse(jsonContent).getAsJsonObject();
			JsonArray array = dataObject.get("data").getAsJsonArray();
			faEvents = new FAEvent[array.size()];

			//Get each FAEvent out of the array
			Gson parser = new Gson();
			for(int i = 0; i < array.size(); i++) {
				faEvents[i] = parser.fromJson(array.get(i), FAEvent.class);
			}
		} catch(Exception e) {
			return null;
		}

		if(is != null)
			try {
				is.close();
			} catch (IOException e) {}
		
		return faEvents;
	}
	
	public String readJSON(InputStream stream) throws IOException, 
														UnsupportedEncodingException {
		InputStream in = stream;
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while(read != null) {
		    sb.append(read);
		    read =br.readLine();
		}

		return sb.toString();
	}
}
