package com.cheatbreaker.client.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.ServerStatusResponse.MinecraftProtocolVersionIdentifier;
import net.minecraft.network.ServerStatusResponse.PlayerCountData;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponseSerializer implements JsonDeserializer, JsonSerializer {
    public JsonElement serialize(ServerStatusResponse serverStatusResponse, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        if (serverStatusResponse.getServerDescription() != null) {
            jsonObject.add("description", jsonSerializationContext.serialize(serverStatusResponse.getServerDescription()));
        }

        if (serverStatusResponse.getPlayerCountData() != null) {
            jsonObject.add("players", jsonSerializationContext.serialize(serverStatusResponse.getPlayerCountData()));
        }

        if (serverStatusResponse.getProtocolVersionInfo() != null) {
            jsonObject.add("version", jsonSerializationContext.serialize(serverStatusResponse.getProtocolVersionInfo()));
        }

        if (serverStatusResponse.getFavicon() != null) {
            jsonObject.addProperty("favicon", serverStatusResponse.getFavicon());
        }

        return jsonObject;
    }

    public ServerStatusResponse deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "status");
        ServerStatusResponse serverStatusResponse = new ServerStatusResponse();
        if (elementAsJsonObject.has("description")) {
            serverStatusResponse.setServerDescription((IChatComponent)jsonDeserializationContext.deserialize(elementAsJsonObject.get("description"), IChatComponent.class));
        }

        if (elementAsJsonObject.has("players")) {
            serverStatusResponse.setPlayerCountData((PlayerCountData)jsonDeserializationContext.deserialize(elementAsJsonObject.get("players"), PlayerCountData.class));
        }

        if (elementAsJsonObject.has("version")) {
            serverStatusResponse.setProtocolVersionInfo((MinecraftProtocolVersionIdentifier)jsonDeserializationContext.deserialize(elementAsJsonObject.get("version"), MinecraftProtocolVersionIdentifier.class));
        }

        if (elementAsJsonObject.has("favicon")) {
            serverStatusResponse.setFavicon(JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "favicon"));
        }

        if (elementAsJsonObject.has("lcServer")) {
            serverStatusResponse.text = elementAsJsonObject.get("lcServer").getAsString();
        }

        return serverStatusResponse;
    }

    public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
        return this.serialize((ServerStatusResponse)obj, type, jsonSerializationContext);
    }
}
