package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer {
    public PackMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        IChatComponent deserialize = (IChatComponent)jsonDeserializationContext.deserialize(asJsonObject.get("description"), IChatComponent.class);
        int jsonObjectIntegerFieldValue = JsonUtils.getJsonObjectIntegerFieldValue(asJsonObject, "pack_format");
        return new PackMetadataSection(deserialize, jsonObjectIntegerFieldValue);
    }

    public JsonElement serialize(PackMetadataSection packMetadataSection, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pack_format", packMetadataSection.getPackFormat());
        jsonObject.add("description", jsonSerializationContext.serialize(packMetadataSection.getDescription()));
        return jsonObject;
    }

    @Override
    public String getSectionName() {
        return "pack";
    }

    public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
        return this.serialize((PackMetadataSection)obj, type, jsonSerializationContext);
    }
}
