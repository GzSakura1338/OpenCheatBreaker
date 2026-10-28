package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S33PacketUpdateSign extends Packet {
    public int intValue;
    public int intValue2;
    public int intValue3;
    public String[] stringArray;

    public S33PacketUpdateSign() {
    }

    public S33PacketUpdateSign(int n, int n2, int n3, String[] strs) {
        this.intValue = n;
        this.intValue2 = n2;
        this.intValue3 = n3;
        this.stringArray = new String[]{strs[0], strs[1], strs[2], strs[3]};
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.intValue = data.readInt();
        this.intValue2 = data.readShort();
        this.intValue3 = data.readInt();
        this.stringArray = new String[4];

        for (int i = 0; i < 4; i++) {
            this.stringArray[i] = data.readStringFromBuffer(15);
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.intValue);
        data.writeShort(this.intValue2);
        data.writeInt(this.intValue3);

        for (int i = 0; i < 4; i++) {
            data.writeStringToBuffer(this.stringArray[i]);
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleUpdateSign(this);
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

    public String[] getStringArray() {
        return this.stringArray;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
