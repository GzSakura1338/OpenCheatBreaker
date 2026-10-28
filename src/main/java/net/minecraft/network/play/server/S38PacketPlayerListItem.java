package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S38PacketPlayerListItem extends Packet {
    public String text;
    public boolean flag;
    public int intValue;

    public S38PacketPlayerListItem() {
    }

    public S38PacketPlayerListItem(String str, boolean flag, int n) {
        this.text = str;
        this.flag = flag;
        this.intValue = n;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.text = data.readStringFromBuffer(16);
        this.flag = data.readBoolean();
        this.intValue = data.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.text);
        data.writeBoolean(this.flag);
        data.writeShort(this.intValue);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handlePlayerListItem(this);
    }

    public String getText() {
        return this.text;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public int getIntValue() {
        return this.intValue;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
