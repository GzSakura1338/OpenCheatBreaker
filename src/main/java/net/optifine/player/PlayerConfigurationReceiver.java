package net.optifine.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.optifine.Config;
import net.optifine.http.IFileDownloadListener;

public class PlayerConfigurationReceiver implements IFileDownloadListener {
   public String player = null;

   public PlayerConfigurationReceiver(String str) {
      this.player = str;
   }

   @Override
   public void fileDownloadFinished(String str, byte[] bytes, Throwable throwable) {
      if (bytes != null) {
         try {
            String str2 = new String(bytes, "ASCII");
            JsonParser jsonParser = new JsonParser();
            JsonElement parse = jsonParser.parse(str2);
            PlayerConfigurationParser playerConfigurationParser = new PlayerConfigurationParser(this.player);
            PlayerConfiguration parsePlayerConfiguration = playerConfigurationParser.parsePlayerConfiguration(parse);
            if (parsePlayerConfiguration != null) {
               parsePlayerConfiguration.setInitialized(true);
               PlayerConfigurations.setPlayerConfiguration(this.player, parsePlayerConfiguration);
            }
         } catch (Exception e) {
            Config.dbg("Error parsing configuration: " + str + ", " + e.getClass().getName() + ": " + e.getMessage());
         }
      }
   }
}
