package net.optifine.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpUtils;
import net.optifine.util.Json;

public class PlayerConfigurationParser {
   public String player = null;
   public static final String CONFIG_ITEMS = "items";
   public static final String ITEM_TYPE = "type";
   public static final String ITEM_ACTIVE = "active";

   public PlayerConfigurationParser(String str) {
      this.player = str;
   }

   public PlayerConfiguration parsePlayerConfiguration(JsonElement jsonElement) {
      if (jsonElement == null) {
         throw new JsonParseException("JSON object is null, player: " + this.player);
      }

      JsonObject jsonObject = (JsonObject)jsonElement;
      PlayerConfiguration playerConfiguration = new PlayerConfiguration();
      JsonArray jsonArray = (JsonArray)jsonObject.get("items");
      if (jsonArray != null) {
         for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject2 = (JsonObject)jsonArray.get(i);
            boolean boolean2 = Json.getBoolean(jsonObject2, "active", true);
            if (boolean2) {
               String string = Json.getString(jsonObject2, "type");
               if (string == null) {
                  Config.warn("Item type is null, player: " + this.player);
               } else {
                  String string2 = Json.getString(jsonObject2, "model");
                  if (string2 == null) {
                     string2 = "items/" + string + "/model.cfg";
                  }

                  PlayerItemModel downloadModel = this.downloadModel(string2);
                  if (downloadModel != null) {
                     if (!downloadModel.isUsePlayerTexture()) {
                        String string3 = Json.getString(jsonObject2, "texture");
                        if (string3 == null) {
                           string3 = "items/" + string + "/users/" + this.player + ".png";
                        }

                        BufferedImage downloadTextureImage = this.downloadTextureImage(string3);
                        if (downloadTextureImage == null) {
                           continue;
                        }

                        downloadModel.setTextureImage(downloadTextureImage);
                        ResourceLocation resourceLocation = new ResourceLocation("optifine.net", string3);
                        downloadModel.setTextureLocation(resourceLocation);
                     }

                     playerConfiguration.addPlayerItemModel(downloadModel);
                  }
               }
            }
         }
      }

      return playerConfiguration;
   }

   public BufferedImage downloadTextureImage(String str) {
      String str2 = HttpUtils.getPlayerItemsUrl() + "/" + str;

      try {
         byte[] bytes = HttpPipeline.get(str2, Minecraft.getMinecraft().getProxy());
         return ImageIO.read(new ByteArrayInputStream(bytes));
      } catch (IOException e) {
         Config.warn("Error loading item texture " + str + ": " + e.getClass().getName() + ": " + e.getMessage());
         return null;
      }
   }

   public PlayerItemModel downloadModel(String str) {
      String str2 = HttpUtils.getPlayerItemsUrl() + "/" + str;

      try {
         byte[] bytes = HttpPipeline.get(str2, Minecraft.getMinecraft().getProxy());
         String str3 = new String(bytes, "ASCII");
         JsonParser jsonParser = new JsonParser();
         JsonObject parse = (JsonObject)jsonParser.parse(str3);
         return PlayerItemParser.parseItemModel(parse);
      } catch (Exception e) {
         Config.warn("Error loading item model " + str + ": " + e.getClass().getName() + ": " + e.getMessage());
         return null;
      }
   }
}
