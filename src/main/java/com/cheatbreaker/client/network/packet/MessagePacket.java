package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class MessagePacket extends AbstractPacket {
    public String message;

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.message = packetBuffer.readString();
    }

    public String getMessage() {
        return this.message;
    }

    public MessagePacket() {
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handleMessage(this);
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(this.message);
    }

    public MessagePacket(String str) {
        this.message = str;
    }
}
