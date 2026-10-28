package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2BPacketChangeGameState extends Packet {
    public static String[] MESSAGE_NAMES = new String[]{"tile.bed.notValid", null, null, "gameMode.changed"};
    public int state;
    public float value;

    public S2BPacketChangeGameState() {
    }

    public S2BPacketChangeGameState(int stateIn, float f) {
        this.state = stateIn;
        this.value = f;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.state = data.readUnsignedByte();
        this.value = data.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.state);
        data.writeFloat(this.value);
    }

    public void processPacket(INetHandlerPlayClient handler) throws IOException {
        handler.handleChangeGameState(this);
    }

    public int getChangeType() {
        return this.state;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
