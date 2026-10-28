package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S28PacketEffect extends Packet {
    public int soundType;
    public int xPos;
    public int yPos;
    public int zPos;
    public int soundData;
    public boolean serverWide;

    public S28PacketEffect() {
    }

    public S28PacketEffect(int n, int n2, int n3, int n4, int n5, boolean flag) {
        this.soundType = n;
        this.xPos = n2;
        this.yPos = n3;
        this.zPos = n4;
        this.soundData = n5;
        this.serverWide = flag;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.soundType = data.readInt();
        this.xPos = data.readInt();
        this.yPos = data.readByte() & 255;
        this.zPos = data.readInt();
        this.soundData = data.readInt();
        this.serverWide = data.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.soundType);
        data.writeInt(this.xPos);
        data.writeByte(this.yPos & 0xFF);
        data.writeInt(this.zPos);
        data.writeInt(this.soundData);
        data.writeBoolean(this.serverWide);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEffect(this);
    }

    public boolean isSoundServerwide() {
        return this.serverWide;
    }

    public int getSoundType() {
        return this.soundType;
    }

    public int getSoundData() {
        return this.soundData;
    }

    public int getPosX() {
        return this.xPos;
    }

    public int getPosY() {
        return this.yPos;
    }

    public int getPosZ() {
        return this.zPos;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
