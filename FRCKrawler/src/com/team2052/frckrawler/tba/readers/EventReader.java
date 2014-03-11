package com.team2052.frckrawler.tba.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.team2052.frckrawler.tba.types.TBAEvent;

public final class EventReader {
	
	private static final String EVENT_URL = "http://www.thebluealliance.com" +
			"/api/v2/events/?X-TBA-App-Id=frckrawler:frckrawler-scouting-system:v18";
	private static final String IND_EVENT_URL_BOTTOM = "http://www.thebluealliance.com" +
			"/api/v2/event/";
	private static final String IND_EVENT_URL_TOP = 
			"?X-TBA-App-Id=frckrawler:frckrawler-scouting-system:v03";
	
	public EventReader() {}
	
	public TBAEvent[] readEvents() throws IOException {
		//Set up the connection
		URL url = new URL(EVENT_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Start the connection
		conn.connect();
		//Get results
		InputStream is = conn.getInputStream();
		//Parse results
		String tbaContent = readTBAData(is);
		tbaContent = clean(tbaContent);
		String[] eventKeys = tbaContent.split(",");
		return readTBAEvents(eventKeys);
	}
	
	private String readTBAData(InputStream stream) throws IOException, 
														UnsupportedEncodingException {
		InputStream in = stream;
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while(read != null) {
		    sb.append(read);
		    read = br.readLine();
		}
		return sb.toString();
	}
	
	private TBAEvent[] readTBAEvents(String[] keys) throws IOException {
		List<TBAEvent> events = new ArrayList<TBAEvent>();
		for(String key : keys) {
			URL url = new URL(IND_EVENT_URL_BOTTOM + key + IND_EVENT_URL_TOP);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			String tbaContent = readTBAData(is);
			//Convert to a TBAEvent
			Gson parser = new Gson();
			TBAEvent event = parser.fromJson(tbaContent, TBAEvent.class);
			events.add(event);
		}
		return events.toArray(new TBAEvent[0]);
	}
	
	private String clean(String s) {
		s = s.replace("[", "");
		s = s.replace("]", "");
		s = s.replace("\"", "");
		s = s.replace(" ", "");
		s = s.replace("}", "");
		s = s.replace("{", "");
		return s;
	}
}
