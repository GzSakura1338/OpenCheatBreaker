package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListWhitelistEntry extends UserListEntry<GameProfile> {
   public UserListWhitelistEntry(GameProfile profile) {
      super(profile);
   }

   public UserListWhitelistEntry(JsonObject json) {
      super(gameProfileFromJsonObject(json), json);
   }

   @Override
   public void onSerialization(JsonObject data) {
      if (this.getValue() != null) {
         data.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
         data.addProperty("name", this.getValue().getName());
         super.onSerialization(data);
      }
   }

   public static GameProfile gameProfileFromJsonObject(JsonObject json) {
      if (json.has("uuid") && json.has("name")) {
         String asString = json.get("uuid").getAsString();

         UUID uuid;
         try {
            uuid = UUID.fromString(asString);
         } catch (Throwable e) {
            return null;
         }

         return new GameProfile(uuid, json.get("name").getAsString());
      } else {
         return null;
      }
   }
}
