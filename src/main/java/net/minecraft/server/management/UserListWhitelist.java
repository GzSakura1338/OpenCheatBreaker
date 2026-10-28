package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListWhitelist extends UserList {
    public UserListWhitelist(File file) {
        super(file);
    }

    @Override
    public UserListEntry createEntry(JsonObject entryData) {
        return new UserListWhitelistEntry(entryData);
    }

    @Override
    public String[] getKeys() {
        String[] strs = new String[this.getValues().size()];
        int n = 0;

        for (UserListWhitelistEntry userListWhitelistEntry : (Iterable<UserListWhitelistEntry>)this.getValues().values()) {
            strs[n++] = ((GameProfile)userListWhitelistEntry.getValue()).getName();
        }

        return strs;
    }

    public String getObjectKey(GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }

    public GameProfile getProfile(String str) {
        for (UserListWhitelistEntry userListWhitelistEntry : (Iterable<UserListWhitelistEntry>)this.getValues().values()) {
            if (str.equalsIgnoreCase(((GameProfile)userListWhitelistEntry.getValue()).getName())) {
                return (GameProfile)userListWhitelistEntry.getValue();
            }
        }

        return null;
    }

    @Override
    public String getObjectKey(Object obj) {
        return this.getObjectKey((GameProfile)obj);
    }
}
