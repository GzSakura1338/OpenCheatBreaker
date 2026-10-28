package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C12PacketUpdateSign extends Packet {
    public int x;
    public int y;
    public int z;
    public String[] lines;

    public C12PacketUpdateSign() {
    }

    public C12PacketUpdateSign(int n, int n2, int n3, String[] strs) {
        this.x = n;
        this.y = n2;
        this.z = n3;
        this.lines = new String[]{strs[0], strs[1], strs[2], strs[3]};
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.x = data.readInt();
        this.y = data.readShort();
        this.z = data.readInt();
        this.lines = new String[4];

        for (int i = 0; i < 4; i++) {
            this.lines[i] = data.readStringFromBuffer(15);
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.x);
        data.writeShort(this.y);
        data.writeInt(this.z);

        for (int i = 0; i < 4; i++) {
            data.writeStringToBuffer(this.lines[i]);
        }
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processUpdateSign(this);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public String[] getLines() {
        return this.lines;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
