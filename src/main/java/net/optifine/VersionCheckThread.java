package net.optifine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.client.ClientBrandRetriever;

public class VersionCheckThread extends Thread {
   public VersionCheckThread() {
      super("VersionCheck");
   }

   @Override
   public void run() {
      HttpURLConnection httpURLConnection = null;

      try {
         Config.dbg("Checking for new version");
         URL url = new URL("http://optifine.net/version/1.8.9/HD_U.txt");
         httpURLConnection = (HttpURLConnection)url.openConnection();
         if (Config.getGameSettings().snooperEnabled) {
            httpURLConnection.setRequestProperty("OF-MC-Version", "1.8.9");
            httpURLConnection.setRequestProperty("OF-MC-Brand", "" + ClientBrandRetriever.getClientModName());
            httpURLConnection.setRequestProperty("OF-Edition", "HD_U");
            httpURLConnection.setRequestProperty("OF-Release", "M6_pre2");
            httpURLConnection.setRequestProperty("OF-Java-Version", "" + System.getProperty("java.version"));
            httpURLConnection.setRequestProperty("OF-CpuCount", "" + Config.getAvailableProcessors());
            httpURLConnection.setRequestProperty("OF-OpenGL-Version", "" + Config.openGlVersion);
            httpURLConnection.setRequestProperty("OF-OpenGL-Vendor", "" + Config.openGlVendor);
         }

         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(false);
         httpURLConnection.connect();

         try {
            InputStream inputStream = httpURLConnection.getInputStream();
            String readInputStream = Config.readInputStream(inputStream);
            inputStream.close();
            String[] tokenize = Config.tokenize(readInputStream, "\n\r");
            if (tokenize.length < 1) {
               return;
            }

            String trim = tokenize[0].trim();
            Config.dbg("Version found: " + trim);
            if (Config.compareRelease(trim, "M6_pre2") > 0) {
               Config.setNewRelease(trim);
               return;
            }
         } finally {
            if (httpURLConnection != null) {
               httpURLConnection.disconnect();
            }
         }
      } catch (Exception e) {
         Config.dbg(e.getClass().getName() + ": " + e.getMessage());
      }
   }
}
