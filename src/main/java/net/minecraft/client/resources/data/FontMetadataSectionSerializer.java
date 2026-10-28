package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class FontMetadataSectionSerializer extends BaseMetadataSectionSerializer {
    public FontMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        float[] floats = new float[256];
        float[] floats2 = new float[256];
        float[] floats3 = new float[256];
        float f = 1.0F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (asJsonObject.has("characters")) {
            if (!asJsonObject.get("characters").isJsonObject()) {
                throw new JsonParseException("Invalid font->characters: expected object, was " + asJsonObject.get("characters"));
            }

            JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("characters");
            if (asJsonObject2.has("default")) {
                if (!asJsonObject2.get("default").isJsonObject()) {
                    throw new JsonParseException("Invalid font->characters->default: expected object, was " + asJsonObject2.get("default"));
                }

                JsonObject asJsonObject3 = asJsonObject2.getAsJsonObject("default");
                f = JsonUtils.getJsonObjectFloatFieldValueOrDefault(asJsonObject3, "width", f);
                Validate.inclusiveBetween(0.0F, Float.MAX_VALUE, f, "Invalid default width", new Object[0]);
                f2 = JsonUtils.getJsonObjectFloatFieldValueOrDefault(asJsonObject3, "spacing", f2);
                Validate.inclusiveBetween(0.0F, Float.MAX_VALUE, f2, "Invalid default spacing", new Object[0]);
                f3 = JsonUtils.getJsonObjectFloatFieldValueOrDefault(asJsonObject3, "left", f2);
                Validate.inclusiveBetween(0.0F, Float.MAX_VALUE, f3, "Invalid default left", new Object[0]);
            }

            for (int i = 0; i < 256; i++) {
                JsonElement jsonElement2 = asJsonObject2.get(Integer.toString(i));
                float f4 = f;
                float f5 = f2;
                float f6 = f3;
                if (jsonElement2 != null) {
                    JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement2, "characters[" + i + "]");
                    f4 = JsonUtils.getJsonObjectFloatFieldValueOrDefault(elementAsJsonObject, "width", f);
                    Validate.inclusiveBetween(0.0F, Float.MAX_VALUE, f4, "Invalid width", new Object[0]);
                    f5 = JsonUtils.getJsonObjectFloatFieldValueOrDefault(elementAsJsonObject, "spacing", f2);
                    Validate.inclusiveBetween(0.0F, Float.MAX_VALUE, f5, "Invalid spacing", new Object[0]);
                    f6 = JsonUtils.getJsonObjectFloatFieldValueOrDefault(elementAsJsonObject, "left", f3);
                    Validate.inclusiveBetween(0.0F, Float.MAX_VALUE, f6, "Invalid left", new Object[0]);
                }

                floats[i] = f4;
                floats2[i] = f5;
                floats3[i] = f6;
            }
        }

        return new FontMetadataSection(floats, floats3, floats2);
    }

    @Override
    public String getSectionName() {
        return "font";
    }
}
