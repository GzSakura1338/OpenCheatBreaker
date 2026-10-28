package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C11PacketEnchantItem extends Packet {
    public int id;
    public int button;

    public C11PacketEnchantItem() {
    }

    public C11PacketEnchantItem(int n, int n2) {
        this.id = n;
        this.button = n2;
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processEnchantItem(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readByte();
        this.button = data.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.id);
        data.writeByte(this.button);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, button=%d", this.id, this.button);
    }

    public int getId() {
        return this.id;
    }

    public int getButton() {
        return this.button;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
