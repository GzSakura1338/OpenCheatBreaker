package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Set;

public class JsonSerializableSet extends ForwardingSet implements IJsonSerializable {
    public Set underlyingSet = Sets.newHashSet();

    @Override
    public void read(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
                this.add(jsonElement2.getAsString());
            }
        }
    }

    @Override
    public JsonElement getSerializableElement() {
        JsonArray jsonArray = new JsonArray();

        for (String str : (Iterable<String>)this) {
            jsonArray.add(new JsonPrimitive(str));
        }

        return jsonArray;
    }

    public Set delegate() {
        return this.underlyingSet;
    }
}
