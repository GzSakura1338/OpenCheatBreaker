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

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer {
    public AnimationMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        ArrayList newArrayList = Lists.newArrayList();
        JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "metadata section");
        int n = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(elementAsJsonObject, "frametime", 1);
        if (n != 1) {
            Validate.inclusiveBetween(1, Integer.MAX_VALUE, n, "Invalid default frame time", new Object[0]);
        }

        if (elementAsJsonObject.has("frames")) {
            try {
                JsonArray jsonObjectJsonArrayField = JsonUtils.getJsonObjectJsonArrayField(elementAsJsonObject, "frames");

                for (int i = 0; i < jsonObjectJsonArrayField.size(); i++) {
                    JsonElement jsonElement2 = jsonObjectJsonArrayField.get(i);
                    AnimationFrame parseAnimationFrame = this.parseAnimationFrame(i, jsonElement2);
                    if (parseAnimationFrame != null) {
                        newArrayList.add(parseAnimationFrame);
                    }
                }
            } catch (ClassCastException e) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + elementAsJsonObject.get("frames"), e);
            }
        }

        int n2 = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(elementAsJsonObject, "width", -1);
        int n3 = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(elementAsJsonObject, "height", -1);
        if (n2 != -1) {
            Validate.inclusiveBetween(1, Integer.MAX_VALUE, n2, "Invalid width", new Object[0]);
        }

        if (n3 != -1) {
            Validate.inclusiveBetween(1, Integer.MAX_VALUE, n3, "Invalid height", new Object[0]);
        }

        return new AnimationMetadataSection(newArrayList, n2, n3, n);
    }

    public AnimationFrame parseAnimationFrame(int n, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new AnimationFrame(JsonUtils.getJsonElementIntegerValue(jsonElement, "frames[" + n + "]"));
        }

        if (jsonElement.isJsonObject()) {
            JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "frames[" + n + "]");
            int n2 = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(elementAsJsonObject, "time", -1);
            if (elementAsJsonObject.has("time")) {
                Validate.inclusiveBetween(1, Integer.MAX_VALUE, n2, "Invalid frame time", new Object[0]);
            }

            int jsonObjectIntegerFieldValue = JsonUtils.getJsonObjectIntegerFieldValue(elementAsJsonObject, "index");
            Validate.inclusiveBetween(0, Integer.MAX_VALUE, jsonObjectIntegerFieldValue, "Invalid frame index", new Object[0]);
            return new AnimationFrame(jsonObjectIntegerFieldValue, n2);
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

    public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
        return this.serialize((AnimationMetadataSection)obj, type, jsonSerializationContext);
    }
}
