package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList {
    public UserListOps(File file) {
        super(file);
    }

    @Override
    public UserListEntry createEntry(JsonObject entryData) {
        return new UserListOpsEntry(entryData);
    }

    @Override
    public String[] getKeys() {
        String[] strs = new String[this.getValues().size()];
        int n = 0;

        for (UserListOpsEntry userListOpsEntry : (Iterable<UserListOpsEntry>)this.getValues().values()) {
            strs[n++] = ((GameProfile)userListOpsEntry.getValue()).getName();
        }

        return strs;
    }

    public String getObjectKey(GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }

    public GameProfile getGameProfileFromName(String str) {
        for (UserListOpsEntry userListOpsEntry : (Iterable<UserListOpsEntry>)this.getValues().values()) {
            if (str.equalsIgnoreCase(((GameProfile)userListOpsEntry.getValue()).getName())) {
                return (GameProfile)userListOpsEntry.getValue();
            }
        }

        return null;
    }

    @Override
    public String getObjectKey(Object obj) {
        return this.getObjectKey((GameProfile)obj);
    }
}
