package com.cheatbreaker.client.friend;

import com.cheatbreaker.client.friends.Friend;

public class FriendBuilder {
    public String status;
    public int iconColor;
    public long offlineSince;
    public String playerId;
    public String name;
    public String server;
    public boolean online;
    public FriendOnlineStatus friendOnlineStatus;

    @Override
    public String toString() {
        return "Friend.FriendBuilder(playerId="
            + this.playerId
            + ", name="
            + this.name
            + ", iconColor="
            + this.iconColor
            + ", status="
            + this.status
            + ", server="
            + this.server
            + ", online="
            + this.online
            + ", offlineSince="
            + this.offlineSince
            + ", onlineStatus="
            + this.friendOnlineStatus
            + ")";
    }

    public FriendBuilder setServer(String str) {
        this.server = str;
        return this;
    }

    public FriendBuilder setOnline(boolean flag) {
        this.online = flag;
        return this;
    }

    public FriendBuilder setFriendOnlineStatus(FriendOnlineStatus friendOnlineStatus) {
        this.friendOnlineStatus = friendOnlineStatus;
        return this;
    }

    public FriendBuilder setIconColor(int n) {
        this.iconColor = n;
        return this;
    }

    public Friend createFriend() {
        return new Friend(this.playerId, this.name, this.iconColor, this.status, this.server, this.online, this.offlineSince, this.friendOnlineStatus);
    }

    public FriendBuilder setStatus(String str) {
        this.status = str;
        return this;
    }

    public FriendBuilder setPlayerId(String str) {
        this.playerId = str;
        return this;
    }

    public FriendBuilder setOfflineSince(long l) {
        this.offlineSince = l;
        return this;
    }

    public FriendBuilder setName(String str) {
        this.name = str;
        return this;
    }
}
