package net.optifine.player;

import com.cheatbreaker.client.CheatBreaker;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.optifine.http.FileDownloadThread;
import net.optifine.http.HttpUtils;

public class PlayerConfigurations {
   public static Map mapConfigurations = null;
   public static boolean reloadPlayerItems = Boolean.getBoolean("player.models.reload");
   public static long timeReloadPlayerItemsMs = System.currentTimeMillis();

   public static void renderPlayerItems(ModelBiped modelBiped, AbstractClientPlayer abstractClientPlayer, float f, float f2) {
      if (CheatBreaker.getInstance().getKeybindSettings().showOptifineHats.getBooleanValue()) {
         PlayerConfiguration playerConfiguration = getPlayerConfiguration(abstractClientPlayer);
         if (playerConfiguration != null) {
            playerConfiguration.renderPlayerItems(modelBiped, abstractClientPlayer, f, f2);
         }
      }
   }

   public static synchronized PlayerConfiguration getPlayerConfiguration(AbstractClientPlayer abstractClientPlayer) {
      if (reloadPlayerItems && System.currentTimeMillis() > timeReloadPlayerItemsMs + 5000L) {
         EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
         if (thePlayer != null) {
            setPlayerConfiguration(thePlayer.getNameClear(), (PlayerConfiguration)null);
            timeReloadPlayerItemsMs = System.currentTimeMillis();
         }
      }

      String nameClear = abstractClientPlayer.getNameClear();
      if (nameClear == null) {
         return null;
      }

      PlayerConfiguration playerConfiguration = (PlayerConfiguration)getMapConfigurations().get(nameClear);
      if (playerConfiguration == null) {
         playerConfiguration = new PlayerConfiguration();
         getMapConfigurations().put(nameClear, playerConfiguration);
         PlayerConfigurationReceiver playerConfigurationReceiver = new PlayerConfigurationReceiver(nameClear);
         String str = HttpUtils.getPlayerItemsUrl() + "/users/" + nameClear + ".cfg";
         FileDownloadThread fileDownloadThread = new FileDownloadThread(str, playerConfigurationReceiver);
         fileDownloadThread.start();
      }

      return playerConfiguration;
   }

   public static synchronized void setPlayerConfiguration(String str, PlayerConfiguration playerConfiguration) {
      getMapConfigurations().put(str, playerConfiguration);
   }

   public static Map getMapConfigurations() {
      if (mapConfigurations == null) {
         mapConfigurations = new HashMap();
      }

      return mapConfigurations;
   }
}
