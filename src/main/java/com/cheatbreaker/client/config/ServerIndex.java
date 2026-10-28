package com.cheatbreaker.client.config;

import com.cheatbreaker.client.util.WebFetcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class ServerIndex {
    public static JsonArray jsonArray;

    public static String[] getStringArray(String str) {
        for (JsonElement jsonElement : jsonArray) {
            String replaceAll = jsonElement.getAsJsonObject().get("id").toString().replaceAll("\"", "");
            if (Objects.equals(str, replaceAll)) {
                String replaceAll2 = jsonElement.getAsJsonObject().get("name").toString().replaceAll("\"", "");
                String str2 = "";

                try {
                    str2 = jsonElement.getAsJsonObject().get("primaryAddress").toString().replaceAll("\"", "");
                } catch (Exception e) {
                    for (JsonElement jsonElement2 : (JsonArray)jsonElement.getAsJsonObject().get("addresses")) {
                        if (str2.isEmpty()) {
                            str2 = jsonElement2.toString().replaceAll("\"", "");
                        }
                    }
                }

                return new String[]{replaceAll2, str2};
            }
        }

        return null;
    }

    public static String lookupByAddress(String str, String str2) {
        try {
            for (JsonElement jsonElement : jsonArray) {
                String replaceAll = jsonElement.getAsJsonObject().get(str2).toString().replaceAll("\"", "");

                for (JsonElement jsonElement2 : (JsonArray)jsonElement.getAsJsonObject().get("addresses")) {
                    if (str.endsWith(jsonElement2.toString().replaceAll("\"", ""))) {
                        return replaceAll;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getPrimaryAddress(String str) {
        for (JsonElement jsonElement : jsonArray) {
            String replaceAll = jsonElement.getAsJsonObject().get("id").toString().replaceAll("\"", "");
            if (Objects.equals(str, replaceAll)) {
                try {
                    return jsonElement.getAsJsonObject().get("primaryAddress").toString().replaceAll("\"", "");
                } catch (Exception e) {
                    JsonArray jsonArray = (JsonArray)jsonElement.getAsJsonObject().get("addresses");
                    Iterator iterator = jsonArray.iterator();
                    if (iterator.hasNext()) {
                        JsonElement jsonElement2 = (JsonElement)iterator.next();
                        return jsonElement2.toString().replaceAll("\"", "");
                    }
                }
            }
        }

        return null;
    }

    static {
        try {
            jsonArray = (JsonArray)new JsonParser()
                .parse(
                    new BufferedReader(
                        new InputStreamReader(
                            WebFetcher.openStream(new URL("https://client-api.cheatbreaker.net/mappings/index.json"), false), StandardCharsets.UTF_8
                        )
                    )
                );
        } catch (Exception e) {
            if (com.cheatbreaker.client.CheatBreaker.isOfflineMode()) {
                System.out.println("Offline mode: loading the server index from the local file.");
            } else {
                e.printStackTrace();
                System.out.println("Failed to load Client API, loading from local file instead.");
            }

            try {
                jsonArray = (JsonArray)new JsonParser()
                    .parse(
                        new BufferedReader(
                            new InputStreamReader(
                                Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("client/properties/server_index.json")).getInputStream(),
                                StandardCharsets.UTF_8
                            )
                        )
                    );
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static String[] getStringArray2(String str) {
        try {
            for (JsonElement jsonElement : jsonArray) {
                String replaceAll = jsonElement.getAsJsonObject().get("id").toString().replaceAll("\"", "");
                if (Objects.equals(str, replaceAll)) {
                    JsonArray jsonArray = (JsonArray)jsonElement.getAsJsonObject().get("addresses");
                    ArrayList list = new ArrayList();

                    for (JsonElement jsonElement2 : jsonArray) {
                        list.add(jsonElement2.toString().replaceAll("\"", ""));
                    }

                    String[] strs = new String[list.size()];
                    return (String[])list.toArray(strs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
