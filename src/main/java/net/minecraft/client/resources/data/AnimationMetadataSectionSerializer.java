package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer
   extends BaseMetadataSectionSerializer<AnimationMetadataSection>
   implements JsonSerializer<AnimationMetadataSection> {
   public AnimationMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      ArrayList newArrayList = Lists.newArrayList();
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "metadata section");
      int int2 = JsonUtils.getInt(jsonObject, "frametime", 1);
      if (int2 != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, int2, "Invalid default frame time");
      }

      if (jsonObject.has("frames")) {
         try {
            JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "frames");

            for (int i = 0; i < jsonArray.size(); i++) {
               JsonElement jsonElement2 = jsonArray.get(i);
               AnimationFrame parseAnimationFrame = this.parseAnimationFrame(i, jsonElement2);
               if (parseAnimationFrame != null) {
                  newArrayList.add(parseAnimationFrame);
               }
            }
         } catch (ClassCastException e) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), e);
         }
      }

      int int3 = JsonUtils.getInt(jsonObject, "width", -1);
      int int4 = JsonUtils.getInt(jsonObject, "height", -1);
      if (int3 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, int3, "Invalid width");
      }

      if (int4 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, int4, "Invalid height");
      }

      boolean boolean2 = JsonUtils.getBoolean(jsonObject, "interpolate", false);
      return new AnimationMetadataSection(newArrayList, int3, int4, int2, boolean2);
   }

   public AnimationFrame parseAnimationFrame(int n, JsonElement jsonElement) {
      if (jsonElement.isJsonPrimitive()) {
         return new AnimationFrame(JsonUtils.getInt(jsonElement, "frames[" + n + "]"));
      }

      if (jsonElement.isJsonObject()) {
         JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "frames[" + n + "]");
         int int2 = JsonUtils.getInt(jsonObject, "time", -1);
         if (jsonObject.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, int2, "Invalid frame time");
         }

         int int3 = JsonUtils.getInt(jsonObject, "index");
         Validate.inclusiveBetween(0L, 2147483647L, int3, "Invalid frame index");
         return new AnimationFrame(int3, int2);
      } else {
         return null;
      }
   }

   public JsonElement serialize(AnimationMetadataSection animationMetadataSection, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("frametime", animationMetadataSection.getFrameTime());
      if (animationMetadataSection.getFrameWidth() != -1) {
         jsonObject.addProperty("width", animationMetadataSection.getFrameWidth());
      }

      if (animationMetadataSection.getFrameHeight() != -1) {
         jsonObject.addProperty("height", animationMetadataSection.getFrameHeight());
      }

      if (animationMetadataSection.getFrameCount() > 0) {
         JsonArray jsonArray = new JsonArray();

         for (int i = 0; i < animationMetadataSection.getFrameCount(); i++) {
            if (animationMetadataSection.frameHasTime(i)) {
               JsonObject jsonObject2 = new JsonObject();
               jsonObject2.addProperty("index", animationMetadataSection.getFrameIndex(i));
               jsonObject2.addProperty("time", animationMetadataSection.getFrameTimeSingle(i));
               jsonArray.add(jsonObject2);
            } else {
               jsonArray.add(new JsonPrimitive(animationMetadataSection.getFrameIndex(i)));
            }
         }

         jsonObject.add("frames", jsonArray);
      }

      return jsonObject;
   }

   @Override
   public String getSectionName() {
      return "animation";
   }
}
