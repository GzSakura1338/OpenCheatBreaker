package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0FPacketConfirmTransaction extends Packet {
    public int id;
    public short uid;
    public boolean accepted;

    public C0FPacketConfirmTransaction() {
    }

    public C0FPacketConfirmTransaction(int n, short s, boolean flag) {
        this.id = n;
        this.uid = s;
        this.accepted = flag;
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processConfirmTransaction(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readByte();
        this.uid = data.readShort();
        this.accepted = data.readByte() != 0;
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.id);
        data.writeShort(this.uid);
        data.writeByte(this.accepted ? 1 : 0);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, uid=%d, accepted=%b", this.id, this.uid, this.accepted);
    }

    public int getId() {
        return this.id;
    }

    public short getUid() {
        return this.uid;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
