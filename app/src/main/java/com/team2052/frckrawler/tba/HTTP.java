package com.team2052.frckrawler.tba;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Adam
 */
public class HTTP {
    public static final String TBA_APP_HEADER = "frc2052:frckrawler-scouting-system:v3";
    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
            client.setReadTimeout(10, TimeUnit.SECONDS);
            client.networkInterceptors().add(new StethoInterceptor());
        }
        return client;
    }

    public static Response getResponse(String url) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("X-TBA-App-Id", TBA_APP_HEADER);
        Request request = requestBuilder.build();
        try {
            return getClient().newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dataFromResponse(Response response) {
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
