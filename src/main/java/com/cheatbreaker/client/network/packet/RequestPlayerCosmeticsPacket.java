package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class RequestPlayerCosmeticsPacket extends AbstractPacket {
    public String playerId;

    public String getPlayerId() {
        return this.playerId;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(this.playerId);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((ServerPacketHandler)packetHandler).handleRequestPlayerCosmetics(this);
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.playerId = packetBuffer.readString();
    }

    public RequestPlayerCosmeticsPacket() {
    }

    public RequestPlayerCosmeticsPacket(String str) {
        this.playerId = str;
    }
}
