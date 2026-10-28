package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;

public class ServerChatMessagePacket extends AbstractPacket {
    public String text;
    public boolean flag;
    public boolean flag2;

    public boolean isFlag2() {
        return this.flag2;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.text = packetBuffer.readString();
        this.flag = packetBuffer.getByteBuf().readBoolean();
        this.flag2 = packetBuffer.getByteBuf().readBoolean();
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handleServerChatMessage(this);
    }

    public String getText() {
        return this.text;
    }

    public ServerChatMessagePacket(String str, boolean flag, boolean flag2) {
        this.text = str;
        this.flag = flag;
        this.flag2 = flag2;
    }

    public ServerChatMessagePacket() {
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(this.text);
        packetBuffer.getByteBuf().writeBoolean(this.flag);
        packetBuffer.getByteBuf().writeBoolean(this.flag2);
    }

    public boolean isFlag() {
        return this.flag;
    }
}
