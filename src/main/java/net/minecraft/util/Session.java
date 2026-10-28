package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;

public class Session {
    public String username;
    public String playerID;
    public String token;
    public Session.Type sessionType;

    public Session(String str, String str2, String str3, String str4) {
        this.username = str;
        this.playerID = str2;
        this.token = str3;
        this.sessionType = Session.Type.setSessionType(str4);
    }

    public String getSessionID() {
        return "token:" + this.token + ":" + this.playerID;
    }

    public String getPlayerID() {
        return this.playerID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public GameProfile getProfile() {
        try {
            UUID fromString = UUIDTypeAdapter.fromString(this.getPlayerID());
            return new GameProfile(fromString, this.getUsername());
        } catch (IllegalArgumentException e) {
            return new GameProfile(null, this.getUsername());
        }
    }

    public Session.Type getSessionType() {
        return this.sessionType;
    }

    public UUID getUUID() {
        return this.getProfile().getId();
    }

    public enum Type {
        LEGACY("LEGACY", 0, "legacy"),
        MOJANG("MOJANG", 1, "mojang");

        public String sessionType;
        public static Map TYPES = Maps.newHashMap();
        public static Session.Type[] typeArray = new Session.Type[]{LEGACY, MOJANG};

        public static Session.Type setSessionType(String str) {
            return (Session.Type)TYPES.get(str.toLowerCase());
        }

        static {
            for (Session.Type type : values()) {
                TYPES.put(type.sessionType, type);
            }
        }

        Type(String str, int n, String str2) {
            this.sessionType = str2;
        }
    }
}
