package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import java.util.ArrayList;
import java.util.List;

public class RecentCosmeticsPacket extends AbstractPacket {
    public List<String> values = new ArrayList<>();

    @Override
    public void read(PacketBuffer packetBuffer) {
        int readInt = packetBuffer.getByteBuf().readInt();
        this.values = new ArrayList<>();

        for (int i = 0; i < readInt; i++) {
            this.values.add(packetBuffer.readString());
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handleRecentCosmetics(this);
    }

    public RecentCosmeticsPacket() {
    }

    public RecentCosmeticsPacket(List<String> list) {
        this.values = list;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.getByteBuf().writeInt(this.values.size());

        for (String str : this.values) {
            packetBuffer.writeString(str);
        }
    }

    public List<String> getValues() {
        return this.values;
    }
}
