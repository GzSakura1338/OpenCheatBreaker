package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
   public UserListOps(File saveFile) {
      super(saveFile);
   }

   @Override
   public UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListOpsEntry(entryData);
   }

   @Override
   public String[] getKeys() {
      String[] strs = new String[this.getValues().size()];
      int n = 0;

      for (UserListOpsEntry userListOpsEntry : this.getValues().values()) {
         strs[n++] = userListOpsEntry.getValue().getName();
      }

      return strs;
   }

   public boolean bypassesPlayerLimit(GameProfile profile) {
      UserListOpsEntry entry = this.getEntry(profile);
      return entry != null ? entry.bypassesPlayerLimit() : false;
   }

   public String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public GameProfile getGameProfileFromName(String username) {
      for (UserListOpsEntry userListOpsEntry : this.getValues().values()) {
         if (username.equalsIgnoreCase(userListOpsEntry.getValue().getName())) {
            return userListOpsEntry.getValue();
         }
      }

      return null;
   }
}
