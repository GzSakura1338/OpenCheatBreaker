package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class PlayEmoteRequestPacket extends AbstractPacket {
    public int emoteId;

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handlePlayEmoteRequest(this);
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.emoteId = packetBuffer.getByteBuf().readInt();
    }

    public PlayEmoteRequestPacket() {
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.getByteBuf().writeInt(this.emoteId);
    }

    public PlayEmoteRequestPacket(int n) {
        this.emoteId = n;
    }

    public int getEmoteId() {
        return this.emoteId;
    }
}
