package com.cheatbreaker.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebFetcher {
   public static InputStream findInputStream(URL url, boolean flag) {
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
