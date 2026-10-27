package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListBans extends UserList<GameProfile, UserListBansEntry> {
   public UserListBans(File bansFile) {
      super(bansFile);
   }

   @Override
   public UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListBansEntry(entryData);
   }

   public boolean isBanned(GameProfile profile) {
      return this.hasEntry(profile);
   }

   @Override
   public String[] getKeys() {
      String[] strs = new String[this.getValues().size()];
      int n = 0;

      for (UserListBansEntry userListBansEntry : this.getValues().values()) {
         strs[n++] = userListBansEntry.getValue().getName();
      }

      return strs;
   }

   public String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public GameProfile isUsernameBanned(String username) {
      for (UserListBansEntry userListBansEntry : this.getValues().values()) {
         if (username.equalsIgnoreCase(userListBansEntry.getValue().getName())) {
            return userListBansEntry.getValue();
         }
      }

      return null;
   }
}
