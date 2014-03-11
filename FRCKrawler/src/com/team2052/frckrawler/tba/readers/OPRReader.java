package com.team2052.frckrawler.tba.readers;

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
import com.team2052.frckrawler.tba.types.TBAOPR;

public class OPRReader {
	
	private static final String oprOverallURL = "http://www.thefirstalliance.org/" +
			"api/api.json.php?action=team-opr&team-number=";
	private static final String oprEventURL1 = "http://www.thefirstalliance.org/" +
			"api/api.json.php?action=team-event-opr&team-number=";
	private static final String oprEventURL2 = "&event-code=";
	
	
	public TBAOPR readOPR(int teamNum, String eventCode, boolean atEvent) throws IOException {
		URL url;
		if(atEvent)
			url = new URL(oprEventURL1 + teamNum + oprEventURL2 + eventCode);
		else
			url = new URL(oprOverallURL + teamNum);
		
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
		JsonObject dataObject = jParser.parse(jsonContent).getAsJsonObject();
		JsonObject oprObject;
		try {
			oprObject = dataObject.get("data").getAsJsonObject();
		} catch(IllegalStateException e) {
			JsonArray arr = dataObject.get("data").getAsJsonArray();
			oprObject = arr.get(1).getAsJsonObject();
		}
	    
	    //Get each FATeam out of the array
		Gson parser = new Gson();
		TBAOPR opr = parser.fromJson(oprObject, TBAOPR.class);

		if(is != null)
			try {
				is.close();
			} catch (IOException e) {}
		
		return opr;
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
