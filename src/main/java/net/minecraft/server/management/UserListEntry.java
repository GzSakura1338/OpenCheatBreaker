package net.minecraft.server.management;

import com.google.gson.JsonObject;

public class UserListEntry {
    public Object value;

    public UserListEntry(Object obj) {
        this.value = obj;
    }

    public UserListEntry(Object obj, JsonObject jsonObject) {
        this.value = obj;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean hasBanExpired() {
        return false;
    }

    public void onSerialization(JsonObject data) {
    }
}
