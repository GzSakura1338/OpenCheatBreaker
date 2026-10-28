package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.util.JsonUtils;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer {
    public LanguageMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        HashSet newHashSet = Sets.newHashSet();

        for (Entry entry : asJsonObject.entrySet()) {
            String key = (String)entry.getKey();
            JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject((JsonElement)entry.getValue(), "language");
            String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "region");
            String jsonObjectStringFieldValue2 = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "name");
            boolean flag = JsonUtils.getJsonObjectBooleanFieldValueOrDefault(elementAsJsonObject, "bidirectional", false);
            if (jsonObjectStringFieldValue.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + key + "'->region: empty value");
            }

            if (jsonObjectStringFieldValue2.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + key + "'->name: empty value");
            }

            if (!newHashSet.add(new Language(key, jsonObjectStringFieldValue, jsonObjectStringFieldValue2, flag))) {
                throw new JsonParseException("Duplicate language->'" + key + "' defined");
            }
        }

        return new LanguageMetadataSection(newHashSet);
    }

    @Override
    public String getSectionName() {
        return "language";
    }
}
