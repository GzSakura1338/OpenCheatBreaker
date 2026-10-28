package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListBans extends UserList {
    public UserListBans(File bansFile) {
        super(bansFile);
    }

    @Override
    public UserListEntry createEntry(JsonObject entryData) {
        return new UserListBansEntry(entryData);
    }

    public boolean isBanned(GameProfile profile) {
        return this.hasEntry(profile);
    }

    @Override
    public String[] getKeys() {
        String[] strs = new String[this.getValues().size()];
        int n = 0;

        for (UserListBansEntry userListBansEntry : (Iterable<UserListBansEntry>)this.getValues().values()) {
            strs[n++] = ((GameProfile)userListBansEntry.getValue()).getName();
        }

        return strs;
    }

    public String getObjectKey(GameProfile profile) {
        return profile.getId().toString();
    }

    public GameProfile isUsernameBanned(String username) {
        for (UserListBansEntry userListBansEntry : (Iterable<UserListBansEntry>)this.getValues().values()) {
            if (username.equalsIgnoreCase(((GameProfile)userListBansEntry.getValue()).getName())) {
                return (GameProfile)userListBansEntry.getValue();
            }
        }

        return null;
    }

    @Override
    public String getObjectKey(Object obj) {
        return this.getObjectKey((GameProfile)obj);
    }
}
