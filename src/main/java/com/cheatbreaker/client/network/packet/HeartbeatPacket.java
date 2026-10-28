package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;

public class HeartbeatPacket extends AbstractPacket {
    @Override
    public void write(PacketBuffer packetBuffer) {
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((ServerPacketHandler)packetHandler).handleHeartbeat(this);
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
    }
}
