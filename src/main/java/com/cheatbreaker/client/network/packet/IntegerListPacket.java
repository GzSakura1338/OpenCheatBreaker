package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;
import java.util.ArrayList;
import java.util.List;

public class IntegerListPacket extends AbstractPacket {
    public List<Integer> values = new ArrayList<>();

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handleIntegerList(this);
    }

    public IntegerListPacket() {
    }

    public IntegerListPacket(List<Integer> list) {
        this.values = list;
    }

    public List<Integer> getValues() {
        return this.values;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        int readInt = packetBuffer.getByteBuf().readInt();
        this.values = new ArrayList<>();

        for (int i = 0; i < readInt; i++) {
            this.values.add(packetBuffer.getByteBuf().readInt());
        }
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.getByteBuf().writeInt(this.values.size());

        for (int n : this.values) {
            packetBuffer.getByteBuf().writeInt(n);
        }
    }
}
