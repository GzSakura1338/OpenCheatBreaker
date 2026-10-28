package net.minecraft.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse {
    public IChatComponent serverMotd;
    public ServerStatusResponse.PlayerCountData playerCount;
    public ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersion;
    public String favicon;
    public String text;

    public IChatComponent getServerDescription() {
        return this.serverMotd;
    }

    public void setServerDescription(IChatComponent motd) {
        this.serverMotd = motd;
    }

    public ServerStatusResponse.PlayerCountData getPlayerCountData() {
        return this.playerCount;
    }

    public void setPlayerCountData(ServerStatusResponse.PlayerCountData countData) {
        this.playerCount = countData;
    }

    public ServerStatusResponse.MinecraftProtocolVersionIdentifier getProtocolVersionInfo() {
        return this.protocolVersion;
    }

    public void setProtocolVersionInfo(ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersionData) {
        this.protocolVersion = protocolVersionData;
    }

    public void setFavicon(String faviconBlob) {
        this.favicon = faviconBlob;
    }

    public String getFavicon() {
        return this.favicon;
    }

    public static class MinecraftProtocolVersionIdentifier {
        public String name;
        public int protocol;

        public MinecraftProtocolVersionIdentifier(String nameIn, int protocolIn) {
            this.name = nameIn;
            this.protocol = protocolIn;
        }

        public String getName() {
            return this.name;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static class Serializer implements JsonDeserializer, JsonSerializer {
            public ServerStatusResponse.MinecraftProtocolVersionIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
                JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "version");
                return new ServerStatusResponse.MinecraftProtocolVersionIdentifier(
                    JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "name"), JsonUtils.getJsonObjectIntegerFieldValue(elementAsJsonObject, "protocol")
                );
            }

            public JsonElement serialize(ServerStatusResponse.MinecraftProtocolVersionIdentifier minecraftProtocolVersionIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", minecraftProtocolVersionIdentifier.getName());
                jsonObject.addProperty("protocol", minecraftProtocolVersionIdentifier.getProtocol());
                return jsonObject;
            }

            public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
                return this.serialize((ServerStatusResponse.MinecraftProtocolVersionIdentifier)obj, type, jsonSerializationContext);
            }
        }
    }

    public static class PlayerCountData {
        public int maxPlayers;
        public int onlinePlayerCount;
        public GameProfile[] players;

        public PlayerCountData(int n, int n2) {
            this.maxPlayers = n;
            this.onlinePlayerCount = n2;
        }

        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        public int getOnlinePlayerCount() {
            return this.onlinePlayerCount;
        }

        public GameProfile[] getPlayers() {
            return this.players;
        }

        public void setPlayers(GameProfile[] playersIn) {
            this.players = playersIn;
        }

        public static class Serializer implements JsonDeserializer, JsonSerializer {
            public ServerStatusResponse.PlayerCountData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
                JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "players");
                ServerStatusResponse.PlayerCountData playerCountData = new ServerStatusResponse.PlayerCountData(
                    JsonUtils.getJsonObjectIntegerFieldValue(elementAsJsonObject, "max"), JsonUtils.getJsonObjectIntegerFieldValue(elementAsJsonObject, "online")
                );
                if (JsonUtils.jsonObjectFieldTypeIsArray(elementAsJsonObject, "sample")) {
                    JsonArray jsonObjectJsonArrayField = JsonUtils.getJsonObjectJsonArrayField(elementAsJsonObject, "sample");
                    if (jsonObjectJsonArrayField.size() > 0) {
                        GameProfile[] gameProfiles = new GameProfile[jsonObjectJsonArrayField.size()];

                        for (int i = 0; i < gameProfiles.length; i++) {
                            JsonObject elementAsJsonObject2 = JsonUtils.getElementAsJsonObject(jsonObjectJsonArrayField.get(i), "player[" + i + "]");
                            String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject2, "id");
                            gameProfiles[i] = new GameProfile(UUID.fromString(jsonObjectStringFieldValue), JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject2, "name"));
                        }

                        playerCountData.setPlayers(gameProfiles);
                    }
                }

                return playerCountData;
            }

            public JsonElement serialize(ServerStatusResponse.PlayerCountData playerCountData, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("max", playerCountData.getMaxPlayers());
                jsonObject.addProperty("online", playerCountData.getOnlinePlayerCount());
                if (playerCountData.getPlayers() != null && playerCountData.getPlayers().length > 0) {
                    JsonArray jsonArray = new JsonArray();

                    for (int i = 0; i < playerCountData.getPlayers().length; i++) {
                        JsonObject jsonObject2 = new JsonObject();
                        UUID id = playerCountData.getPlayers()[i].getId();
                        jsonObject2.addProperty("id", id == null ? "" : id.toString());
                        jsonObject2.addProperty("name", playerCountData.getPlayers()[i].getName());
                        jsonArray.add(jsonObject2);
                    }

                    jsonObject.add("sample", jsonArray);
                }

                return jsonObject;
            }

            public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
                return this.serialize((ServerStatusResponse.PlayerCountData)obj, type, jsonSerializationContext);
            }
        }
    }

    public static class Serializer implements JsonDeserializer, JsonSerializer {
        public ServerStatusResponse deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "status");
            ServerStatusResponse serverStatusResponse = new ServerStatusResponse();
            if (elementAsJsonObject.has("description")) {
                serverStatusResponse.setServerDescription((IChatComponent)jsonDeserializationContext.deserialize(elementAsJsonObject.get("description"), IChatComponent.class));
            }

            if (elementAsJsonObject.has("players")) {
                serverStatusResponse.setPlayerCountData((ServerStatusResponse.PlayerCountData)jsonDeserializationContext.deserialize(elementAsJsonObject.get("players"), ServerStatusResponse.PlayerCountData.class));
            }

            if (elementAsJsonObject.has("version")) {
                serverStatusResponse.setProtocolVersionInfo(
                    (ServerStatusResponse.MinecraftProtocolVersionIdentifier)jsonDeserializationContext.deserialize(
                        elementAsJsonObject.get("version"), ServerStatusResponse.MinecraftProtocolVersionIdentifier.class
                    )
                );
            }

            if (elementAsJsonObject.has("favicon")) {
                serverStatusResponse.setFavicon(JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "favicon"));
            }

            if (elementAsJsonObject.has("lcServer")) {
                serverStatusResponse.text = elementAsJsonObject.get("lcServer").getAsString();
            }

            return serverStatusResponse;
        }

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

        public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((ServerStatusResponse)obj, type, jsonSerializationContext);
        }
    }
}
