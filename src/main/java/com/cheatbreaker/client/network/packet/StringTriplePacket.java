package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class StringTriplePacket extends AbstractPacket {
    public String text;
    public String text2;
    public String text3;

    public String getText2() {
        return this.text2;
    }

    public StringTriplePacket(String str, String str2, String str3) {
        this.text = str;
        this.text2 = str2;
        this.text3 = str3;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(this.text);
        packetBuffer.writeString(this.text2);
        packetBuffer.writeString(this.text3);
    }

    public String getText() {
        return this.text;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.text = packetBuffer.readString();
        this.text2 = packetBuffer.readString();
        this.text3 = packetBuffer.readString();
    }

    public StringTriplePacket() {
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handleStringTriple(this);
    }

    public String getText3() {
        return this.text3;
    }
}
