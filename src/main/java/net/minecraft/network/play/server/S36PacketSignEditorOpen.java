package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S36PacketSignEditorOpen extends Packet {
    public int intValue;
    public int intValue2;
    public int intValue3;

    public S36PacketSignEditorOpen() {
    }

    public S36PacketSignEditorOpen(int n, int n2, int n3) {
        this.intValue = n;
        this.intValue2 = n2;
        this.intValue3 = n3;
    }

    public void processPacket(INetHandlerPlayClient handler) throws IOException {
        handler.handleSignEditorOpen(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.intValue = data.readInt();
        this.intValue2 = data.readInt();
        this.intValue3 = data.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.intValue);
        data.writeInt(this.intValue2);
        data.writeInt(this.intValue3);
    }

    public int getIntValue() {
        return this.intValue;
    }

    public int getIntValue2() {
        return this.intValue2;
    }

    public int getIntValue3() {
        return this.intValue3;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
