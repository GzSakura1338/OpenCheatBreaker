package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class StopEmotePacket extends AbstractPacket {
    @Override
    public void handle(PacketHandler packetHandler) {
        ((ServerPacketHandler)packetHandler).handleStopEmote(this);
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
    }
}
