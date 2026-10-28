package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.friend.OwnOnlineStatus;

public class OnlineFriendData {
    public String playerId;
    public String name;
    public OwnOnlineStatus ownOnlineStatus;
    public String server;

    public String getName() {
        return this.name;
    }

    public OnlineFriendData(String str, String str2, OwnOnlineStatus ownOnlineStatus, String str3) {
        this.playerId = str;
        this.name = str2;
        this.ownOnlineStatus = ownOnlineStatus;
        this.server = str3;
    }

    public String getServer() {
        return this.server;
    }

    public OwnOnlineStatus getOwnOnlineStatus() {
        return this.ownOnlineStatus;
    }

    public String getPlayerId() {
        return this.playerId;
    }
}
