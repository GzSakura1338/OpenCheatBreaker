package com.cheatbreaker.client.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebFetcher {
    public static String readAll(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();

        int n;
        while ((n = reader.read()) != -1) {
            sb.append((char)n);
        }

        return sb.toString();
    }

    public static JsonArray findJsonArray(String str) throws java.net.MalformedURLException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(str).openStream(), StandardCharsets.UTF_8));
        String readAll2 = readAll(bufferedReader);
        return JsonParser.parseString(readAll2).getAsJsonArray();
    }

    public void noop() {
    }

    public static JsonObject findJsonObject(String str) throws java.net.MalformedURLException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(str).openStream(), StandardCharsets.UTF_8));
        String readAll2 = readAll(bufferedReader);
        return JsonParser.parseString(readAll2).getAsJsonObject();
    }

    public static InputStream openStream(URL url, boolean flag) {
        if (com.cheatbreaker.client.CheatBreaker.isOfflineMode()) {
            throw new RuntimeException(new IOException("offline mode"));
        }

        try {
            HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
            openConnection.addRequestProperty("User-Agent", flag ? "CheatBreaker-Client" : "Mozilla/4.0");
            return openConnection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
