package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.util.JsonUtils;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer<TextureMetadataSection> {
   public TextureMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      boolean boolean2 = JsonUtils.getBoolean(asJsonObject, "blur", false);
      boolean boolean3 = JsonUtils.getBoolean(asJsonObject, "clamp", false);
      ArrayList newArrayList = Lists.newArrayList();
      if (asJsonObject.has("mipmaps")) {
         try {
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("mipmaps");

            for (int i = 0; i < asJsonArray.size(); i++) {
               JsonElement jsonElement2 = asJsonArray.get(i);
               if (jsonElement2.isJsonPrimitive()) {
                  try {
                     newArrayList.add(jsonElement2.getAsInt());
                  } catch (NumberFormatException e) {
                     throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonElement2, e);
                  }
               } else if (jsonElement2.isJsonObject()) {
                  throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonElement2);
               }
            }
         } catch (ClassCastException e) {
            throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + asJsonObject.get("mipmaps"), e);
         }
      }

      return new TextureMetadataSection(boolean2, boolean3, newArrayList);
   }

   @Override
   public String getSectionName() {
      return "texture";
   }
}
