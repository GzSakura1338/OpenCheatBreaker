package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class UserListBansEntry extends BanEntry {
    public UserListBansEntry(GameProfile gameProfile) {
        this(gameProfile, null, null, null, null);
    }

    public UserListBansEntry(GameProfile gameProfile, Date date, String str, Date date2, String str2) {
        super(gameProfile, date2, str, date2, str2);
    }

    public UserListBansEntry(JsonObject jsonObject) {
        super(getProfileFromJson(jsonObject), jsonObject);
    }

    @Override
    public void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
            data.addProperty("name", ((GameProfile)this.getValue()).getName());
            super.onSerialization(data);
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
