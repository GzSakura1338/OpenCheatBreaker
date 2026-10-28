package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class FriendRequestsPacket extends AbstractPacket {
    public String json;

    public FriendRequestsPacket() {
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.json = packetBuffer.readString();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handleFriendRequests(this);
    }

    public FriendRequestsPacket(String str) {
        this.json = str;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(this.json);
    }

    public String getJson() {
        return this.json;
    }
}
