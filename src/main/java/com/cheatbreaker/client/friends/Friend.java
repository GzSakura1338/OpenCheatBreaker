package com.cheatbreaker.client.friends;

import com.cheatbreaker.client.friend.FriendBuilder;
import com.cheatbreaker.client.friend.FriendOnlineStatus;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.util.EnumChatFormatting;

public class Friend {
    public String playerId;
    public String name;
    public int iconColor;
    public String status;
    public String server;
    public boolean online;
    public long offlineSince;
    public FriendOnlineStatus onlineStatus;

    public String getPlayerId() {
        return this.playerId;
    }

    public boolean isOnline() {
        return this.online;
    }

    public Friend(String str, String str2, int n, String str3, String str4, boolean flag, long l, FriendOnlineStatus friendOnlineStatus) {
        this.playerId = str;
        this.name = str2;
        this.iconColor = n;
        this.status = str3;
        this.server = str4;
        this.online = flag;
        this.offlineSince = l;
        this.onlineStatus = friendOnlineStatus;
    }

    public static int getStatusColor(FriendOnlineStatus friendOnlineStatus) {
        if (friendOnlineStatus == null) {
            return -13158601;
        }

        int n;
        switch (friendOnlineStatus) {
            case AWAY:
                n = new Color(-1722581).getRGB();
                break;
            case BUSY:
                n = new Color(-1758421).getRGB();
                break;
            case OFFLINE:
                n = new Color(-13158601).getRGB();
                break;
            default:
                n = -13369549;
        }

        return n;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Friend)) {
            return false;
        }

        Friend friend = (Friend)obj;
        if (!friend.checkObject(this)) {
            return false;
        }

        if (this.getIconColor() != friend.getIconColor()) {
            return false;
        }

        if (this.isOnline() != friend.isOnline()) {
            return false;
        }

        if (this.getOfflineSince() != friend.getOfflineSince()) {
            return false;
        }

        String playerId = this.getPlayerId();
        String playerId2 = friend.getPlayerId();
        if (playerId == null ? playerId2 == null : playerId.equals(playerId2)) {
            String name = this.getName();
            String name2 = friend.getName();
            if (name == null ? name2 == null : name.equals(name2)) {
                String status = this.getStatus();
                String status2 = friend.getStatus();
                if (status == null ? status2 == null : status.equals(status2)) {
                    String server = this.getServer();
                    String server2 = friend.getServer();
                    if (server == null ? server2 == null : server.equals(server2)) {
                        FriendOnlineStatus onlineStatus = this.getOnlineStatus();
                        FriendOnlineStatus onlineStatus2 = friend.getOnlineStatus();
                        return onlineStatus == null ? onlineStatus2 == null : onlineStatus.equals(onlineStatus2);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setOfflineSince(long l) {
        this.offlineSince = l;
    }

    public int getIntValue() {
        return new Color(this.iconColor).getRGB();
    }

    public String getServer() {
        return this.server;
    }

    public String getStatusText() {
        String str;
        if (this.online) {
            if (this.server != null && !Objects.equals(this.server, "")) {
                str = "Playing" + EnumChatFormatting.BOLD + " " + this.server;
            } else {
                switch (this.onlineStatus) {
                    case AWAY:
                        str = "Away";
                        break;
                    case BUSY:
                        str = "Busy";
                        break;
                    case OFFLINE:
                        str = "Offline";
                        break;
                    default:
                        str = "Online";
                }
            }
        } else {
            long l = System.currentTimeMillis() - this.offlineSince;
            long l2 = 1000L;
            long l3 = l2 * 60L;
            long l4 = l3 * 60L;
            long l5 = l4 * 24L;
            long l6 = l / l5;
            long l7;
            long l8 = (l7 = l % l5) / l4;
            long l9 = (l = l7 % l4) / l3;
            str = l6 > 0L
                ? "Offline for " + l6 + (l6 == 1L ? " day" : " days")
                : (l8 > 0L ? "Offline for " + l8 + (l8 == 1L ? " hour" : " hours") : "Offline for " + l9 + (l9 == 1L ? " minute" : " minutes"));
        }

        return str;
    }

    public void setServer(String str) {
        this.server = str;
    }

    public int getIconColor() {
        return this.iconColor;
    }

    @Override
    public int hashCode() {
        byte b = 59;
        int n = 1;
        n = n * 59 + this.getIconColor();
        n = n * 59 + (this.isOnline() ? 79 : 97);
        long offlineSince = this.getOfflineSince();
        n = n * 59 + (int)(offlineSince >>> 32 ^ offlineSince);
        String playerId = this.getPlayerId();
        n = n * 59 + (playerId == null ? 43 : playerId.hashCode());
        String name = this.getName();
        n = n * 59 + (name == null ? 43 : name.hashCode());
        String status = this.getStatus();
        n = n * 59 + (status == null ? 43 : status.hashCode());
        String server = this.getServer();
        n = n * 59 + (server == null ? 43 : server.hashCode());
        FriendOnlineStatus onlineStatus = this.getOnlineStatus();
        return n * 59 + (onlineStatus == null ? 43 : onlineStatus.hashCode());
    }

    public FriendOnlineStatus getOnlineStatus() {
        return this.onlineStatus;
    }

    public void setName(String str) {
        this.name = str;
    }

    @Override
    public String toString() {
        return "Friend(playerId="
            + this.getPlayerId()
            + ", name="
            + this.getName()
            + ", iconColor="
            + this.getIconColor()
            + ", status="
            + this.getStatus()
            + ", server="
            + this.getServer()
            + ", online="
            + this.isOnline()
            + ", offlineSince="
            + this.getOfflineSince()
            + ", onlineStatus="
            + this.getOnlineStatus()
            + ")";
    }

    public String getName() {
        return this.name;
    }

    public static FriendBuilder createFriendBuilder() {
        return new FriendBuilder();
    }

    public void setStatus(String str) {
        this.status = str;
    }

    public String getStatus() {
        return this.status;
    }

    public long getOfflineSince() {
        return this.offlineSince;
    }

    public boolean checkObject(Object obj) {
        return obj instanceof Friend;
    }

    public void setOnline(boolean flag) {
        this.online = flag;
    }

    public void setOnlineStatus(FriendOnlineStatus friendOnlineStatus) {
        this.onlineStatus = friendOnlineStatus;
    }

    public void setIconColor(int n) {
        this.iconColor = n;
    }
}
