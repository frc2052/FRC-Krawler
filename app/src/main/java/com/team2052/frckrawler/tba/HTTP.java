package com.team2052.frckrawler.tba;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Adam
 */
public class HTTP {
    public static HttpResponse getResponse(String url){
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("X-TBA-App_Id", "frckrawler:frckrawler-scouting-system:v2");
            return httpClient.execute(httpGet);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String dataFromResponse(HttpResponse response) {
        InputStream is;
        String result;
        // Read response to string
        if(response != null) {
            try {
                HttpEntity entity = response.getEntity();

                is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();
                result = sb.toString();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


}
