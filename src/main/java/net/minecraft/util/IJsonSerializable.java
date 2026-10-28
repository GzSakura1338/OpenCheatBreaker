package net.minecraft.util;

import com.google.gson.JsonElement;

public interface IJsonSerializable {
    void read(JsonElement jsonElement);

    JsonElement getSerializableElement();
}
