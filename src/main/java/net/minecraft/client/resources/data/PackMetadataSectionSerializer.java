package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer<PackMetadataSection> implements JsonSerializer<PackMetadataSection> {
   public PackMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      IChatComponent deserialize = (IChatComponent)jsonDeserializationContext.deserialize(asJsonObject.get("description"), IChatComponent.class);
      if (deserialize == null) {
         throw new JsonParseException("Invalid/missing description!");
      }

      int int2 = JsonUtils.getInt(asJsonObject, "pack_format");
      return new PackMetadataSection(deserialize, int2);
   }

   public JsonElement serialize(PackMetadataSection packMetadataSection, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("pack_format", packMetadataSection.getPackFormat());
      jsonObject.add("description", jsonSerializationContext.serialize(packMetadataSection.getPackDescription()));
      return jsonObject;
   }

   @Override
   public String getSectionName() {
      return "pack";
   }
}
