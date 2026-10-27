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

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer<LanguageMetadataSection> {
   public LanguageMetadataSection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      HashSet newHashSet = Sets.newHashSet();

      for (Entry entry : asJsonObject.entrySet()) {
         String key = (String)entry.getKey();
         JsonObject jsonObject = JsonUtils.getJsonObject((JsonElement)entry.getValue(), "language");
         String string = JsonUtils.getString(jsonObject, "region");
         String string2 = JsonUtils.getString(jsonObject, "name");
         boolean boolean2 = JsonUtils.getBoolean(jsonObject, "bidirectional", false);
         if (string.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + key + "'->region: empty value");
         }

         if (string2.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + key + "'->name: empty value");
         }

         if (!newHashSet.add(new Language(key, string, string2, boolean2))) {
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
