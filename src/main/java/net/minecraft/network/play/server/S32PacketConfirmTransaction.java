package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S32PacketConfirmTransaction extends Packet {
    public int id;
    public short actionId;
    public boolean accepted;

    public S32PacketConfirmTransaction() {
    }

    public S32PacketConfirmTransaction(int n, short s, boolean flag) {
        this.id = n;
        this.actionId = s;
        this.accepted = flag;
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleConfirmTransaction(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readUnsignedByte();
        this.actionId = data.readShort();
        this.accepted = data.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.id);
        data.writeShort(this.actionId);
        data.writeBoolean(this.accepted);
    }

    @Override
    public String serialize() {
        return String.format("id=%d, uid=%d, accepted=%b", this.id, this.actionId, this.accepted);
    }

    public int getId() {
        return this.id;
    }

    public short getActionId() {
        return this.actionId;
    }

    public boolean wasAccepted() {
        return this.accepted;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
