package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListOpsEntry extends UserListEntry {
    public int permissionLevel;

    public UserListOpsEntry(GameProfile gameProfile, int n) {
        super(gameProfile);
        this.permissionLevel = n;
    }

    public UserListOpsEntry(JsonObject jsonObject) {
        super(getProfileFromJson(jsonObject), jsonObject);
        this.permissionLevel = jsonObject.has("level") ? jsonObject.get("level").getAsInt() : 0;
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    @Override
    public void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
            data.addProperty("name", ((GameProfile)this.getValue()).getName());
            super.onSerialization(data);
            data.addProperty("level", this.permissionLevel);
        }
    }

    public static GameProfile getProfileFromJson(JsonObject jsonObject) {
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
