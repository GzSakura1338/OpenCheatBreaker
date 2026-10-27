package net.minecraft.server.management;

import com.google.gson.JsonObject;

public class UserListEntry<T> {
   public T value;

   public UserListEntry(T valueIn) {
      this.value = (T)valueIn;
   }

   public UserListEntry(T valueIn, JsonObject json) {
      this.value = (T)valueIn;
   }

   public T getValue() {
      return this.value;
   }

   public boolean hasBanExpired() {
      return false;
   }

   public void onSerialization(JsonObject data) {
   }
}
