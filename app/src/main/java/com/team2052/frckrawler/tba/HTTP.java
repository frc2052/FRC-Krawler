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
    public static final String TBA_APP_HEADER = "MTnxl1VUPAEseVOl8UGFhM7MjFJfJ1hNMxGd4BF8Lyt3fzqpKkFynemdPMHDyyXA";
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
                .addHeader("X-TBA-Auth-Key", TBA_APP_HEADER);
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
