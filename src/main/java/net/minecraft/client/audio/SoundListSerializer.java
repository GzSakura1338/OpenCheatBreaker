package net.minecraft.client.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import net.minecraft.client.audio.SoundList.SoundEntry;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class SoundListSerializer implements JsonDeserializer {
    public SoundList deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "entry");
        SoundList soundList = new SoundList();
        soundList.setReplaceExisting(JsonUtils.getJsonObjectBooleanFieldValueOrDefault(elementAsJsonObject, "replace", false));
        SoundCategory byName = SoundCategory.byName(JsonUtils.getJsonObjectStringFieldValueOrDefault(elementAsJsonObject, "category", SoundCategory.MASTER.getCategoryName()));
        soundList.setSoundCategory(byName);
        Validate.notNull(byName, "Invalid category", new Object[0]);
        if (elementAsJsonObject.has("sounds")) {
            JsonArray jsonObjectJsonArrayField = JsonUtils.getJsonObjectJsonArrayField(elementAsJsonObject, "sounds");

            for (int i = 0; i < jsonObjectJsonArrayField.size(); i++) {
                JsonElement jsonElement2 = jsonObjectJsonArrayField.get(i);
                SoundEntry soundEntry = new SoundEntry();
                if (JsonUtils.jsonElementTypeIsString(jsonElement2)) {
                    soundEntry.setSoundEntryName(JsonUtils.getJsonElementStringValue(jsonElement2, "sound"));
                } else {
                    JsonObject elementAsJsonObject2 = JsonUtils.getElementAsJsonObject(jsonElement2, "sound");
                    soundEntry.setSoundEntryName(JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject2, "name"));
                    if (elementAsJsonObject2.has("type")) {
                        net.minecraft.client.audio.SoundList.SoundEntry.Type type2 = net.minecraft.client.audio.SoundList.SoundEntry.Type.getType(
                            JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject2, "type")
                        );
                        Validate.notNull(type2, "Invalid type", new Object[0]);
                        soundEntry.setSoundEntryType(type2);
                    }

                    if (elementAsJsonObject2.has("volume")) {
                        float jsonObjectFloatFieldValue = JsonUtils.getJsonObjectFloatFieldValue(elementAsJsonObject2, "volume");
                        Validate.isTrue(jsonObjectFloatFieldValue > 0.0F, "Invalid volume", new Object[0]);
                        soundEntry.setSoundEntryVolume(jsonObjectFloatFieldValue);
                    }

                    if (elementAsJsonObject2.has("pitch")) {
                        float jsonObjectFloatFieldValue = JsonUtils.getJsonObjectFloatFieldValue(elementAsJsonObject2, "pitch");
                        Validate.isTrue(jsonObjectFloatFieldValue > 0.0F, "Invalid pitch", new Object[0]);
                        soundEntry.setSoundEntryPitch(jsonObjectFloatFieldValue);
                    }

                    if (elementAsJsonObject2.has("weight")) {
                        int jsonObjectIntegerFieldValue = JsonUtils.getJsonObjectIntegerFieldValue(elementAsJsonObject2, "weight");
                        Validate.isTrue(jsonObjectIntegerFieldValue > 0, "Invalid weight", new Object[0]);
                        soundEntry.setSoundEntryWeight(jsonObjectIntegerFieldValue);
                    }

                    if (elementAsJsonObject2.has("stream")) {
                        soundEntry.setStreaming(JsonUtils.getJsonObjectBooleanFieldValue(elementAsJsonObject2, "stream"));
                    }
                }

                soundList.getSoundList().add(soundEntry);
            }
        }

        return soundList;
    }
}
