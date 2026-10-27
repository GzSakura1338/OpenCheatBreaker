package com.cheatbreaker.client.radio;

import com.cheatbreaker.client.audio.ResourceJavaLayerHook;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javazoom.jl.decoder.JavaLayerUtils;
import javazoom.jl.player.Player;

public class RadioApi {
   public static Player player;
   public static boolean playbackStarting;
   public static final String API_URL = "https://dash-api.com/api/v3/allData.php";
   public static RadioVolume volume = new RadioVolume();

   public static Object fetch(String str, boolean flag) {
      try {
         URLConnection openConnection = new URL(str).openConnection();
         openConnection.setConnectTimeout(5000);
         openConnection.setReadTimeout(5000);
         if (flag) {
            return openConnection.getInputStream();
         }

         try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()))) {
            StringBuilder sb = new StringBuilder();

            String str2;
            while ((str2 = bufferedReader.readLine()) != null) {
               sb.append(str2);
            }

            return sb.toString();
         }
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static List<Station> loadStations() {
      if (com.cheatbreaker.client.CheatBreaker.isOfflineMode()) {
         return new ArrayList<>();
      }

      JavaLayerUtils.setHook(new ResourceJavaLayerHook());
      ArrayList list = new ArrayList();

      try {
         JsonObject asJsonObject = new JsonParser().parse((String)fetch("https://dash-api.com/api/v3/allData.php", false)).getAsJsonObject();
         if (asJsonObject.has("stations")) {
            for (JsonElement jsonElement : asJsonObject.getAsJsonArray("stations")) {
               JsonObject asJsonObject2 = jsonElement.getAsJsonObject();
               String asString = asJsonObject2.get("name").getAsString();
               String asString2 = asJsonObject2.get("genre").getAsString();
               String asString3 = asJsonObject2.get("square_logo_url").getAsString();
               String asString4 = asJsonObject2.get("current_song_url").getAsString();
               String asString5 = asJsonObject2.get("stream_url").getAsString();
               Station station = new Station(asString, asString3, asString2, asString4, asString5);
               list.add(station);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return list;
   }

   public static void play(String str) {
      if (!playbackStarting) {
         playbackStarting = true;
         if (player != null) {
            player.close();
            player = null;
         } else {
            new Thread(() -> {
               try {
                  InputStream fetch2 = (InputStream)fetch(str, true);
                  if (fetch2 == null) {
                     throw new Exception("Failed to fetch stream data.");
                  }

                  volume = new RadioVolume();
                  player = new Player(fetch2, volume);
                  player.play();
               } catch (Exception e) {
                  e.printStackTrace();
                  playbackStarting = false;
               }
            }).start();
         }
      }
   }

   public static void stop() {
      if (player != null) {
         player.close();
         player = null;
      }

      playbackStarting = false;
   }

   public static RadioVolume getVolume() {
      return volume;
   }

   public static boolean isPlaying() {
      return player != null;
   }
}
