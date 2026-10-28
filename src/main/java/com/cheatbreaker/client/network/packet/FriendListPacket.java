package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.friend.OwnOnlineStatus;
import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;
import java.util.ArrayList;
import java.util.List;

public class FriendListPacket extends AbstractPacket {
    public List<OnlineFriendData> onlineFriends = new ArrayList<>();
    public List<OfflineFriendData> offlineFriends = new ArrayList<>();
    public boolean flag;
    public boolean acceptFriendRequests;
    public int onlineCount;
    public int offlineCount;

    public boolean isFlag() {
        return this.flag;
    }

    public FriendListPacket(boolean flag, boolean flag2, int n, int n2, List<OnlineFriendData> list, List<OfflineFriendData> list2) {
        this.flag = flag;
        this.acceptFriendRequests = flag2;
        this.onlineCount = n;
        this.offlineCount = n2;
        this.onlineFriends = list;
        this.offlineFriends = list2;
    }

    public int getOnlineCount() {
        return this.onlineCount;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handleFriendList(this);
    }

    public int getOfflineCount() {
        return this.offlineCount;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.getByteBuf().writeBoolean(this.flag);
        packetBuffer.getByteBuf().writeBoolean(this.acceptFriendRequests);
        packetBuffer.getByteBuf().writeInt(this.onlineCount);
        packetBuffer.getByteBuf().writeInt(this.offlineCount);

        for (OnlineFriendData onlineFriendData : this.onlineFriends) {
            packetBuffer.writeString(onlineFriendData.getPlayerId());
            packetBuffer.writeString(onlineFriendData.getName());
            packetBuffer.getByteBuf().writeInt(onlineFriendData.getOwnOnlineStatus().ordinal());
            packetBuffer.writeString(onlineFriendData.getServer());
        }

        for (OfflineFriendData offlineFriendData : this.offlineFriends) {
            packetBuffer.writeString(offlineFriendData.getPlayerId());
            packetBuffer.writeString(offlineFriendData.getName());
            packetBuffer.getByteBuf().writeLong(offlineFriendData.getLastSeen());
        }
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.flag = packetBuffer.getByteBuf().readBoolean();
        this.acceptFriendRequests = packetBuffer.getByteBuf().readBoolean();
        this.onlineCount = packetBuffer.getByteBuf().readInt();
        this.offlineCount = packetBuffer.getByteBuf().readInt();
        this.onlineFriends = new ArrayList<>();

        for (int i = 0; i < this.onlineCount; i++) {
            String readString = packetBuffer.readString();
            String readString2 = packetBuffer.readString();
            int readInt = packetBuffer.getByteBuf().readInt();
            OwnOnlineStatus ownOnlineStatus = OwnOnlineStatus.values()[readInt];
            String readString3 = packetBuffer.readString();
            this.onlineFriends.add(new OnlineFriendData(readString, readString2, ownOnlineStatus, readString3));
        }

        this.offlineFriends = new ArrayList<>();

        for (int i = 0; i < this.offlineCount; i++) {
            String readString = packetBuffer.readString();
            String readString2 = packetBuffer.readString();
            long readLong = packetBuffer.getByteBuf().readLong();
            this.offlineFriends.add(new OfflineFriendData(readString, readString2, readLong));
        }
    }

    public List<OfflineFriendData> getOfflineFriends() {
        return this.offlineFriends;
    }

    public boolean isAcceptFriendRequests() {
        return this.acceptFriendRequests;
    }

    public List<OnlineFriendData> getOnlineFriends() {
        return this.onlineFriends;
    }

    public FriendListPacket() {
    }
}
