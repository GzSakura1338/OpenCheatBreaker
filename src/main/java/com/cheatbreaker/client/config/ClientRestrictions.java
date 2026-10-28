package com.cheatbreaker.client.config;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.impl.AutoTextMod;
import com.cheatbreaker.client.util.ParallelExecutor;
import com.cheatbreaker.client.util.WebFetcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class ClientRestrictions {
    public static ResourceLocation restrictionsJson = new ResourceLocation("client/properties/restrictions.json");
    public final String restrictionsUrl = "https://client-api.cheatbreaker.net/mods/restrictions.json";
    public JsonArray jsonArray;

    public void applyRestrictions() {
        try {
            for (JsonElement jsonElement : this.jsonArray) {
                ArrayList list = new ArrayList();
                String replaceAll = jsonElement.getAsJsonObject().get("id").toString().replaceAll("\"", "");
                String replaceAll2 = jsonElement.getAsJsonObject().get("restrictedMessage").toString().replaceAll("\"", "").replaceAll("\\\\n", System.lineSeparator());
                AbstractMod findAbstractMod = CheatBreaker.getInstance().getModManager().findAbstractMod(replaceAll);
                JsonArray jsonArray = (JsonArray)jsonElement.getAsJsonObject().get("restrictedOptions");
                JsonArray jsonArray2 = (JsonArray)jsonElement.getAsJsonObject().get("restrictedAddresses");
                JsonArray jsonArray3 = (JsonArray)jsonElement.getAsJsonObject().get("restrictedServerMappingIDs");
                if (findAbstractMod != null) {
                    if (jsonArray2 != null) {
                        for (JsonElement jsonElement2 : jsonArray2) {
                            String replaceAll3 = jsonElement2.toString().replaceAll("\"", "");
                            list.add(replaceAll3);
                        }
                    }

                    if (jsonArray != null) {
                        for (JsonElement jsonElement2 : jsonArray) {
                            String replaceAll3 = jsonElement2.toString().replaceAll("\"", "");
                        }
                    }

                    if (jsonArray3 != null) {
                        for (JsonElement jsonElement2 : jsonArray3) {
                            String replaceAll3 = jsonElement2.toString().replaceAll("\"", "");
                            String[] stringArray2 = ServerIndex.getStringArray2(replaceAll3);
                            assert stringArray2 != null;
                            list.addAll(Arrays.asList(stringArray2));
                        }
                    }

                    int size = list.size();
                    String[] toArray = (String[])list.toArray(new String[size]);
                    if (findAbstractMod == CheatBreaker.getInstance().getModManager().autoTextMod) {
                        replaceAll2 = "The allowed commands are: "
                            + Arrays.toString(((AutoTextMod)findAbstractMod).allowedCommands)
                                .replaceAll("\\\\n", System.lineSeparator())
                                .replaceAll("\\[", "")
                                .replaceAll("]", "");
                    }

                    findAbstractMod.setRestriction(replaceAll2, toArray);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public ClientRestrictions() {
        this.fetchRestrictions();
    }

    public void fetchRestrictions() {
        ParallelExecutor.execute(
            () -> {
                try {
                    this.jsonArray = JsonParser.parseReader(
                            new BufferedReader(
                                new InputStreamReader(
                                    WebFetcher.openStream(new URL("https://client-api.cheatbreaker.net/mods/restrictions.json"), false), StandardCharsets.UTF_8
                                )
                            )
                        )
                        .getAsJsonArray();
                    this.applyRestrictions();
                } catch (Exception e) {
                    if (CheatBreaker.isOfflineMode()) {
                        System.out.println("Offline mode: loading the mod restrictions from the local file.");
                    } else {
                        e.printStackTrace();
                        System.out.println("Failed to load Client API, loading from local file instead.");
                    }

                    try {
                        this.jsonArray = JsonParser.parseReader(
                                new BufferedReader(
                                    new InputStreamReader(
                                        Minecraft.getMinecraft().getResourceManager().getResource(restrictionsJson).getInputStream(), StandardCharsets.UTF_8
                                    )
                                )
                            )
                            .getAsJsonArray();
                        this.applyRestrictions();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        );
    }
}
