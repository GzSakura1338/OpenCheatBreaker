package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry> {
   public UserListWhitelist(File file) {
      super(file);
   }

   @Override
   public UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListWhitelistEntry(entryData);
   }

   @Override
   public String[] getKeys() {
      String[] strs = new String[this.getValues().size()];
      int n = 0;

      for (UserListWhitelistEntry userListWhitelistEntry : this.getValues().values()) {
         strs[n++] = userListWhitelistEntry.getValue().getName();
      }

      return strs;
   }

   public String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public GameProfile getBannedProfile(String name) {
      for (UserListWhitelistEntry userListWhitelistEntry : this.getValues().values()) {
         if (name.equalsIgnoreCase(userListWhitelistEntry.getValue().getName())) {
            return userListWhitelistEntry.getValue();
         }
      }

      return null;
   }
}
