package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListWhitelistEntry extends UserListEntry {
    public UserListWhitelistEntry(GameProfile gameProfile) {
        super(gameProfile);
    }

    public UserListWhitelistEntry(JsonObject jsonObject) {
        super(deserializeProfile(jsonObject), jsonObject);
    }

    @Override
    public void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
            data.addProperty("name", ((GameProfile)this.getValue()).getName());
            super.onSerialization(data);
        }
    }

    public static GameProfile deserializeProfile(JsonObject jsonObject) {
        if (jsonObject.has("uuid") && jsonObject.has("name")) {
            String asString = jsonObject.get("uuid").getAsString();

            UUID uuid;
            try {
                uuid = UUID.fromString(asString);
            } catch (Throwable e) {
                return null;
            }

            return new GameProfile(uuid, jsonObject.get("name").getAsString());
        } else {
            return null;
        }
    }
}
