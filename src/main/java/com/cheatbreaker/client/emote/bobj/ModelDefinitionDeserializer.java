package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.MeshMaterial;
import com.cheatbreaker.client.emote.ModelDefinition;
import com.cheatbreaker.client.emote.Transform;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ModelDefinitionDeserializer implements JsonDeserializer<ModelDefinition> {
    public void readTransforms(Map<String, Transform> map, JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
        map.clear();
        if (jsonElement.isJsonArray()) {
            for (String str : toStringArray(jsonElement.getAsJsonArray())) {
                map.put(str, new Transform(str));
            }
        } else if (jsonElement.isJsonObject()) {
            for (Entry entry : ((JsonObject)jsonElement).entrySet()) {
                Transform deserialize = (Transform)jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), Transform.class);
                deserialize.name = (String)entry.getKey();
                map.put(deserialize.name, deserialize);
            }
        }
    }

    public ModelDefinition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        if (!jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        ModelDefinition modelDefinition = new ModelDefinition();
        if (asJsonObject.has("name")) {
            modelDefinition.name = asJsonObject.get("name").getAsString();
        }

        if (asJsonObject.has("primaryMesh")) {
            modelDefinition.primaryMesh = asJsonObject.get("primaryMesh").getAsString();
        }

        if (asJsonObject.has("scale")) {
            modelDefinition.scale = asJsonObject.get("scale").getAsFloat();
        }

        if (asJsonObject.has("scaleGui")) {
            modelDefinition.scaleGUI = asJsonObject.get("scaleGui").getAsFloat();
        }

        if (asJsonObject.has("scaleItems")) {
            modelDefinition.scaleItems = asJsonObject.get("scaleItems").getAsFloat();
        }

        if (asJsonObject.has("renderHeldItems")) {
            modelDefinition.renderHeldItems = asJsonObject.get("renderHeldItems").getAsBoolean();
        }

        if (asJsonObject.has("leftHands")) {
            this.readTransforms(modelDefinition.leftHands, asJsonObject.get("leftHands"), jsonDeserializationContext);
        }

        if (asJsonObject.has("rightHands")) {
            this.readTransforms(modelDefinition.rightHands, asJsonObject.get("rightHands"), jsonDeserializationContext);
        }

        if (asJsonObject.has("head")) {
            modelDefinition.head = asJsonObject.get("head").getAsString();
        }

        if (asJsonObject.has("actions")) {
            modelDefinition.actions = (ActionMap)jsonDeserializationContext.deserialize(asJsonObject.get("actions"), ActionMap.class);
        }

        if (asJsonObject.has("meshes")) {
            HashMap map = new HashMap();

            for (Entry entry : asJsonObject.getAsJsonObject("meshes").entrySet()) {
                map.put(entry.getKey(), jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), MeshMaterial.class));
            }

            modelDefinition.meshMaterials = map;
        }

        return modelDefinition;
    }

    public static String[] toStringArray(JsonArray jsonArray) {
        ArrayList list = new ArrayList();
        int size = jsonArray.size();

        for (int i = 0; i < size; i++) {
            JsonElement jsonElement = jsonArray.get(i);
            if (jsonElement.isJsonPrimitive()) {
                list.add(jsonElement.getAsString());
            }
        }

        return (String[])list.toArray(new String[list.size()]);
    }
}
