package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;
import java.util.ArrayList;
import java.util.List;

public class ProcessListResponsePacket extends AbstractPacket {
    public List<String> lines = new ArrayList<>();

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.getByteBuf().writeInt(this.lines.size());

        for (String str : this.lines) {
            packetBuffer.writeString(str);
        }
    }

    public ProcessListResponsePacket(List<String> list) {
        this.lines = list;
    }

    public List<String> getLines() {
        return this.lines;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        int readInt = packetBuffer.getByteBuf().readInt();
        this.lines = new ArrayList<>();

        for (int i = 0; i < readInt; i++) {
            this.lines.add(packetBuffer.readString());
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((ServerPacketHandler)packetHandler).handleProcessListResponse(this);
    }

    public ProcessListResponsePacket() {
    }
}
