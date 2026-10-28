package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class BooleanStringPacket extends AbstractPacket {
    public boolean flag;
    public String text;

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.flag = packetBuffer.getByteBuf().readBoolean();
        this.text = packetBuffer.readString();
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.getByteBuf().writeBoolean(this.flag);
        packetBuffer.writeString(this.text);
    }

    public String getText() {
        return this.text;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handleBooleanString(this);
    }

    public boolean isFlag() {
        return this.flag;
    }

    public BooleanStringPacket(boolean flag, String str) {
        this.flag = flag;
        this.text = str;
    }

    public BooleanStringPacket() {
    }
}
