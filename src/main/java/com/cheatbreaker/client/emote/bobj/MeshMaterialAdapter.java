package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.MeshMaterial;
import com.cheatbreaker.client.util.ResourceLocationUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class MeshMaterialAdapter implements JsonDeserializer<MeshMaterial>, JsonSerializer<MeshMaterial> {
    public JsonElement serialize(MeshMaterial meshMaterial, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        if (meshMaterial.texture != null) {
            jsonObject.add("texture", ResourceLocationUtils.toJson(meshMaterial.texture));
        }

        jsonObject.addProperty("filtering", meshMaterial.textureFilter == 9729 ? "linear" : "nearest");
        jsonObject.addProperty("normals", meshMaterial.normals);
        jsonObject.addProperty("smooth", meshMaterial.smooth);
        jsonObject.addProperty("visible", meshMaterial.visible);
        jsonObject.addProperty("lighting", meshMaterial.lighting);
        jsonObject.addProperty("color", meshMaterial.color);
        return jsonObject;
    }

    public MeshMaterial deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        if (!jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        MeshMaterial meshMaterial = new MeshMaterial();
        if (asJsonObject.has("texture")) {
            meshMaterial.texture = ResourceLocationUtils.fromJson(asJsonObject.get("texture"));
        }

        if (asJsonObject.has("filtering")) {
            meshMaterial.textureFilter = asJsonObject.get("filtering").getAsString().equalsIgnoreCase("linear") ? 9729 : 9728;
        }

        if (asJsonObject.has("normals")) {
            meshMaterial.normals = asJsonObject.get("normals").getAsBoolean();
        }

        if (asJsonObject.has("smooth")) {
            meshMaterial.smooth = asJsonObject.get("smooth").getAsBoolean();
        }

        if (asJsonObject.has("visible")) {
            meshMaterial.visible = asJsonObject.get("visible").getAsBoolean();
        }

        if (asJsonObject.has("lighting")) {
            meshMaterial.lighting = asJsonObject.get("lighting").getAsBoolean();
        }

        if (asJsonObject.has("color")) {
            meshMaterial.color = asJsonObject.get("color").getAsInt();
        }

        return meshMaterial;
    }
}
