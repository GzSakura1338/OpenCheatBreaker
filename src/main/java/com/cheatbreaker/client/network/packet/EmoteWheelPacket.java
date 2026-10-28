package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;
import java.util.ArrayList;
import java.util.List;

public class EmoteWheelPacket extends AbstractPacket {
    public List<Integer> emoteIds = new ArrayList<>();

    @Override
    public void handle(PacketHandler packetHandler) {
        ((ServerPacketHandler)packetHandler).handleEmoteWheel(this);
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(this.emoteIds.size());

        for (int n : this.emoteIds) {
            packetBuffer.writeVarInt(n);
        }
    }

    public EmoteWheelPacket() {
    }

    public List<Integer> getEmoteIds() {
        return this.emoteIds;
    }

    public EmoteWheelPacket(List<Integer> list) {
        this.emoteIds = list;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        int readVarInt = packetBuffer.readVarInt();
        this.emoteIds = new ArrayList<>();

        for (int i = 0; i < readVarInt; i++) {
            this.emoteIds.add(packetBuffer.readVarInt());
        }
    }
}
