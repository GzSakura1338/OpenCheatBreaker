package net.minecraft.client.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class SoundListSerializer implements JsonDeserializer<SoundList> {
   public SoundList deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "entry");
      SoundList soundList = new SoundList();
      soundList.setReplaceExisting(JsonUtils.getBoolean(jsonObject, "replace", false));
      SoundCategory category = SoundCategory.getCategory(JsonUtils.getString(jsonObject, "category", SoundCategory.MASTER.getCategoryName()));
      soundList.setSoundCategory(category);
      Validate.notNull(category, "Invalid category", new Object[0]);
      if (jsonObject.has("sounds")) {
         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "sounds");

         for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement jsonElement2 = jsonArray.get(i);
            SoundList.SoundEntry soundEntry = new SoundList.SoundEntry();
            if (JsonUtils.isString(jsonElement2)) {
               soundEntry.setSoundEntryName(JsonUtils.getString(jsonElement2, "sound"));
            } else {
               JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonElement2, "sound");
               soundEntry.setSoundEntryName(JsonUtils.getString(jsonObject2, "name"));
               if (jsonObject2.has("type")) {
                  SoundList.SoundEntry.Type type2 = SoundList.SoundEntry.Type.getType(JsonUtils.getString(jsonObject2, "type"));
                  Validate.notNull(type2, "Invalid type", new Object[0]);
                  soundEntry.setSoundEntryType(type2);
               }

               if (jsonObject2.has("volume")) {
                  float float2 = JsonUtils.getFloat(jsonObject2, "volume");
                  Validate.isTrue(float2 > 0.0F, "Invalid volume", new Object[0]);
                  soundEntry.setSoundEntryVolume(float2);
               }

               if (jsonObject2.has("pitch")) {
                  float float2 = JsonUtils.getFloat(jsonObject2, "pitch");
                  Validate.isTrue(float2 > 0.0F, "Invalid pitch", new Object[0]);
                  soundEntry.setSoundEntryPitch(float2);
               }

               if (jsonObject2.has("weight")) {
                  int int2 = JsonUtils.getInt(jsonObject2, "weight");
                  Validate.isTrue(int2 > 0, "Invalid weight", new Object[0]);
                  soundEntry.setSoundEntryWeight(int2);
               }

               if (jsonObject2.has("stream")) {
                  soundEntry.setStreaming(JsonUtils.getBoolean(jsonObject2, "stream"));
               }
            }

            soundList.getSoundList().add(soundEntry);
         }
      }

      return soundList;
   }
}
