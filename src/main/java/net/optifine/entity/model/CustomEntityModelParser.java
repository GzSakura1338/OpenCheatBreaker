package net.optifine.entity.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.config.ConnectedParser;
import net.optifine.entity.model.anim.ModelUpdater;
import net.optifine.entity.model.anim.ModelVariableUpdater;
import net.optifine.player.PlayerItemParser;
import net.optifine.util.Json;

public class CustomEntityModelParser {
   public static final String ENTITY = "entity";
   public static final String TEXTURE = "texture";
   public static final String SHADOW_SIZE = "shadowSize";
   public static final String ITEM_TEXTURE_SIZE = "textureSize";
   public static final String ITEM_USE_PLAYER_TEXTURE = "usePlayerTexture";
   public static final String ITEM_MODELS = "models";
   public static final String ITEM_ANIMATIONS = "animations";
   public static final String MODEL_ID = "id";
   public static final String MODEL_BASE_ID = "baseId";
   public static final String MODEL_MODEL = "model";
   public static final String MODEL_PART = "part";
   public static final String MODEL_ATTACH = "attach";
   public static final String MODEL_INVERT_AXIS = "invertAxis";
   public static final String MODEL_MIRROR_TEXTURE = "mirrorTexture";
   public static final String MODEL_TRANSLATE = "translate";
   public static final String MODEL_ROTATE = "rotate";
   public static final String MODEL_SCALE = "scale";
   public static final String MODEL_BOXES = "boxes";
   public static final String MODEL_SPRITES = "sprites";
   public static final String MODEL_SUBMODEL = "submodel";
   public static final String MODEL_SUBMODELS = "submodels";
   public static final String BOX_TEXTURE_OFFSET = "textureOffset";
   public static final String BOX_COORDINATES = "coordinates";
   public static final String BOX_SIZE_ADD = "sizeAdd";
   public static final String ENTITY_MODEL = "EntityModel";
   public static final String ENTITY_MODEL_PART = "EntityModelPart";
   public static final String TEXT_TYPE = "type";
   public static final String TEXT_TYPE2 = "type";

   public static CustomEntityRenderer parseEntityRender(JsonObject jsonObject, String str) {
      ConnectedParser connectedParser = new ConnectedParser("CustomEntityModels");
      String parseName = connectedParser.parseName(str);
      String parseBasePath = connectedParser.parseBasePath(str);
      String string = Json.getString(jsonObject, "texture");
      int[] parseIntArray = Json.parseIntArray(jsonObject.get("textureSize"), 2);
      float float2 = Json.getFloat(jsonObject, "shadowSize", -1.0F);
      JsonArray jsonArray = (JsonArray)jsonObject.get("models");
      checkNull(jsonArray, "Missing models");
      HashMap map = new HashMap();
      ArrayList list = new ArrayList();

      for (int i = 0; i < jsonArray.size(); i++) {
         JsonObject jsonObject2 = (JsonObject)jsonArray.get(i);
         processBaseId(jsonObject2, map);
         processExternalModel(jsonObject2, map, parseBasePath);
         processId(jsonObject2, map);
         CustomModelRenderer parseCustomModelRenderer2 = parseCustomModelRenderer(jsonObject2, parseIntArray, parseBasePath);
         if (parseCustomModelRenderer2 != null) {
            list.add(parseCustomModelRenderer2);
         }
      }

      CustomModelRenderer[] toArray = (CustomModelRenderer[])list.toArray(new CustomModelRenderer[list.size()]);
      ResourceLocation resourceLocation = null;
      if (string != null) {
         resourceLocation = getResourceLocation(parseBasePath, string, ".png");
      }

      return new CustomEntityRenderer(parseName, parseBasePath, resourceLocation, toArray, float2);
   }

   public static void processBaseId(JsonObject jsonObject, Map map) {
      String string = Json.getString(jsonObject, "baseId");
      if (string != null) {
         JsonObject jsonObject2 = (JsonObject)map.get(string);
         if (jsonObject2 == null) {
            Config.warn("BaseID not found: " + string);
         } else {
            copyJsonElements(jsonObject2, jsonObject);
         }
      }
   }

   public static void processExternalModel(JsonObject jsonObject, Map map, String str) {
      String string = Json.getString(jsonObject, "model");
      if (string != null) {
         ResourceLocation resourceLocation = getResourceLocation(str, string, ".jpm");

         try {
            JsonObject loadJson2 = loadJson(resourceLocation);
            if (loadJson2 == null) {
               Config.warn("Model not found: " + resourceLocation);
               return;
            }

            copyJsonElements(loadJson2, jsonObject);
         } catch (IOException e) {
            Config.error("" + e.getClass().getName() + ": " + e.getMessage());
         } catch (JsonParseException e) {
            Config.error("" + e.getClass().getName() + ": " + e.getMessage());
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public static void copyJsonElements(JsonObject jsonObject, JsonObject jsonObject2) {
      for (Entry entry : jsonObject.entrySet()) {
         if (!((String)entry.getKey()).equals("id") && !jsonObject2.has((String)entry.getKey())) {
            jsonObject2.add((String)entry.getKey(), (JsonElement)entry.getValue());
         }
      }
   }

   public static ResourceLocation getResourceLocation(String str, String str2, String str3) {
      if (!str2.endsWith(str3)) {
         str2 = str2 + str3;
      }

      if (!str2.contains("/")) {
         str2 = str + "/" + str2;
      } else if (str2.startsWith("./")) {
         str2 = str + "/" + str2.substring(2);
      } else if (str2.startsWith("~/")) {
         str2 = "optifine/" + str2.substring(2);
      }

      return new ResourceLocation(str2);
   }

   public static void processId(JsonObject jsonObject, Map map) {
      String string = Json.getString(jsonObject, "id");
      if (string != null) {
         if (string.length() < 1) {
            Config.warn("Empty model ID: " + string);
         } else if (map.containsKey(string)) {
            Config.warn("Duplicate model ID: " + string);
         } else {
            map.put(string, jsonObject);
         }
      }
   }

   public static CustomModelRenderer parseCustomModelRenderer(JsonObject jsonObject, int[] ints, String str) {
      String string = Json.getString(jsonObject, "part");
      checkNull(string, "Model part not specified, missing \"replace\" or \"attachTo\".");
      boolean boolean2 = Json.getBoolean(jsonObject, "attach", false);
      CustomEntityModel customEntityModel = new CustomEntityModel();
      if (ints != null) {
         customEntityModel.textureWidth = ints[0];
         customEntityModel.textureHeight = ints[1];
      }

      ModelUpdater modelUpdater = null;
      JsonArray jsonArray = (JsonArray)jsonObject.get("animations");
      if (jsonArray != null) {
         ArrayList list = new ArrayList();

         for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject2 = (JsonObject)jsonArray.get(i);

            for (Entry entry : jsonObject2.entrySet()) {
               String key = (String)entry.getKey();
               String asString = ((JsonElement)entry.getValue()).getAsString();
               ModelVariableUpdater modelVariableUpdater = new ModelVariableUpdater(key, asString);
               list.add(modelVariableUpdater);
            }
         }

         if (list.size() > 0) {
            ModelVariableUpdater[] toArray = (ModelVariableUpdater[])list.toArray(new ModelVariableUpdater[list.size()]);
            modelUpdater = new ModelUpdater(toArray);
         }
      }

      ModelRenderer parseModelRenderer = PlayerItemParser.parseModelRenderer(jsonObject, customEntityModel, ints, str);
      return new CustomModelRenderer(string, boolean2, parseModelRenderer, modelUpdater);
   }

   public static void checkNull(Object obj, String str) {
      if (obj == null) {
         throw new JsonParseException(str);
      }
   }

   public static JsonObject loadJson(ResourceLocation resourceLocation) throws JsonParseException, IOException {
      InputStream resourceStream = Config.getResourceStream(resourceLocation);
      if (resourceStream == null) {
         return null;
      }

      String readInputStream = Config.readInputStream(resourceStream, "ASCII");
      resourceStream.close();
      JsonParser jsonParser = new JsonParser();
      return (JsonObject)jsonParser.parse(readInputStream);
   }
}
