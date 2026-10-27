package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Set;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable {
   public Set<String> underlyingSet = Sets.newHashSet();

   @Override
   public void fromJson(JsonElement json) {
      if (json.isJsonArray()) {
         for (JsonElement jsonElement : json.getAsJsonArray()) {
            this.add(jsonElement.getAsString());
         }
      }
   }

   @Override
   public JsonElement getSerializableElement() {
      JsonArray jsonArray = new JsonArray();

      for (String str : this) {
         jsonArray.add(new JsonPrimitive(str));
      }

      return jsonArray;
   }

   public Set<String> delegate() {
      return this.underlyingSet;
   }
}
